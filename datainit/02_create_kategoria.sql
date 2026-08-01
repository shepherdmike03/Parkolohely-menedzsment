/*  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/

CREATE TABLE kategoria(
  kategoria_id BIGINT GENERATED ALWAYS AS IDENTITY,       /*generalodjon + legyen ez a fo kulcs*/
  kategoria_nev VARCHAR(100) NOT NULL,
  meret_sorrend INTEGER NOT NULL,

  CONSTRAINT pk_kategoria
    PRIMARY KEY(kategoria_id),

  CONSTRAINT uq_kategoria_nev       /*Kategoria nev legyen egyedi, Motorbicikli, busz, kamion, gepjarmu, elektromos auto etc..*/
    UNIQUE (kategoria_nev),

  CONSTRAINT chk_kategoria_meret_sorrend
    CHECK (meret_sorrend > 0)               /*Motor = 1, Szemelykocsi = 2, TeherAuto = 3*/
  );
