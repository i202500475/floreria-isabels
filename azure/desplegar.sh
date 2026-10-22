#!/usr/bin/env bash
# Despliega la tienda Isabel's en Azure (Azure for Students) desde Azure Cloud Shell.
# Crea: grupo de recursos, MySQL servidor flexible B1ms, plan App Service B1 (Linux) y la aplicación web.
# Uso:  curl -sL https://raw.githubusercontent.com/i202500475/floreria-isabels/main/azure/desplegar.sh | bash
# Se puede volver a ejecutar: reutiliza los nombres y contraseñas guardados en ~/floreria.env
set -euo pipefail

CONF=~/floreria.env
if [ -f "$CONF" ]; then source "$CONF"; else
  SUF=$(tr -dc 'a-z0-9' </dev/urandom | head -c 5)
  RG=floreria-rg
  # Usa la región de una aplicación existente (las suscripciones de estudiante limitan las regiones)
  LOC=$(az webapp list --query "[0].location" -o tsv 2>/dev/null | tr -d ' ' | tr 'A-Z' 'a-z')
  LOC=${LOC:-eastus2}
  APP=floreria-isabels-$SUF
  PLAN=floreria-plan-$SUF
  MYSQL=floreria-mysql-$SUF
  DBUSER=floreriaadmin
  DBPASS="Fl0r$(tr -dc 'A-Za-z0-9' </dev/urandom | head -c 18)!"
  ADMINPASS="Isabels-$(tr -dc 'A-Za-z0-9' </dev/urandom | head -c 8)"
  printf 'SUF=%s\nRG=%s\nLOC=%s\nAPP=%s\nPLAN=%s\nMYSQL=%s\nDBUSER=%s\nDBPASS=%q\nADMINPASS=%q\n' \
    "$SUF" "$RG" "$LOC" "$APP" "$PLAN" "$MYSQL" "$DBUSER" "$DBPASS" "$ADMINPASS" > "$CONF"
fi
LOC=${LOC_OVERRIDE:-$LOC}
echo "== Región: $LOC | App: $APP | MySQL: $MYSQL"

paso() { echo; echo "== $*"; }

paso "1/7 Grupo de recursos"
az group create -n "$RG" -l "$LOC" -o none

paso "2/7 MySQL servidor flexible B1ms (tarda 5 a 10 minutos)"
if ! az mysql flexible-server show -g "$RG" -n "$MYSQL" -o none 2>/dev/null; then
  az mysql flexible-server create -g "$RG" -n "$MYSQL" -l "$LOC" \
    --admin-user "$DBUSER" --admin-password "$DBPASS" \
    --tier Burstable --sku-name Standard_B1ms --storage-size 20 --version 8.0.21 \
    --public-access 0.0.0.0 --yes -o none
fi
az mysql flexible-server db create -g "$RG" -s "$MYSQL" -d floreria -o none 2>/dev/null || true

paso "3/7 Plan App Service B1 (Linux)"
az appservice plan show -g "$RG" -n "$PLAN" -o none 2>/dev/null || \
  az appservice plan create -g "$RG" -n "$PLAN" -l "$LOC" --is-linux --sku B1 -o none

paso "4/7 Aplicación web Java 17"
az webapp show -g "$RG" -n "$APP" -o none 2>/dev/null || \
  az webapp create -g "$RG" -p "$PLAN" -n "$APP" --runtime "JAVA:17-java17" -o none

paso "5/7 Variables de entorno"
HOST=$(az mysql flexible-server show -g "$RG" -n "$MYSQL" --query fullyQualifiedDomainName -o tsv)
az webapp config appsettings set -g "$RG" -n "$APP" -o none --settings \
  "DB_URL=jdbc:mysql://$HOST:3306/floreria?createDatabaseIfNotExist=true&sslMode=REQUIRED&serverTimezone=UTC" \
  "DB_USERNAME=$DBUSER" "DB_PASSWORD=$DBPASS" "ADMIN_PASSWORD=$ADMINPASS" \
  "SERVER_PORT=80" "WEBSITES_PORT=80"
az webapp config set -g "$RG" -n "$APP" --always-on true -o none

paso "6/7 Compilar Angular y el JAR"
rm -rf ~/floreria-build && git clone -q --depth 1 https://github.com/i202500475/floreria-isabels ~/floreria-build
cd ~/floreria-build/floreria-frontend
npm install --no-audit --no-fund --loglevel=error
npx ng build --configuration production --base-href /tienda/
mkdir -p ../ProyectoFloreria/src/main/resources/static/tienda
cp -r dist/floreria-frontend/browser/* ../ProyectoFloreria/src/main/resources/static/tienda/
cd ../ProyectoFloreria
mvn -B -q package -DskipTests
JAR=$(ls target/*.jar | grep -v original | head -1)

paso "7/7 Publicar en App Service"
az webapp deploy -g "$RG" -n "$APP" --src-path "$JAR" --type jar --async false -o none

URL="https://$(az webapp show -g "$RG" -n "$APP" --query defaultHostName -o tsv)"
echo
echo "======================================================"
echo " LISTO. Tienda:   $URL/tienda/"
echo " API:             $URL/api/flores"
echo " Usuario admin:   admin   /   $ADMINPASS"
echo " Repartidor:      Deli    /   very"
echo " (datos guardados en ~/floreria.env)"
echo "======================================================"
echo "FIN-DESPLIEGUE $URL"
