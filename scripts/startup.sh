#!/usr/bin/env bash



#  ____  _                _                  _ __  __ _ _
# / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
# \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
#  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
# |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
#                  |_|



echo "Biztonsagi kapcsolok bekapcsolasa!"
      # biztonsagi kapcsolok
set -euo pipefail
    # -e ---> ha barmelyik parancs hibat terit vissza leall a script
    # -u ---> ha nem definialt valtozohoz probalok hivatkozni
    # -o pipefail ---> tobbes pipe-okban a teljes lanc hibakoddal ter vissza ha elbukik


echo "Postgress AdatBazisra varas ..."
until PGPASSWORD="$DB_PASS" \
  psql \
    -h "$DB_HOST" \
    -p "$DB_PORT" \
    -U "$DB_USER" \
    -d "$DB_NAME" \
    -c '\q' >/dev/null 2>&1 
do
  echo "Az adatbazis meg nem erheto el!"
  sleep 2
done
  # egy ciklus ami addig ismetlodik amig a feltetel nem igaz.
  # TESZT KAPCSOLAT ---> majd azonnal kilep '\q' 
  # Hibat eldobja hogy a log ne legyen zajos

echo "Postgress munkara keszen all!"


#ide fogom majd irni a kovetkezo scriptek meghivasat, ami elinditja majd a fo programot!



