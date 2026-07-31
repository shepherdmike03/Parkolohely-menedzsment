/*  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/


CREATE TABLE parkolohely (
  parkolohely_id BIGINT GENERATED ALWAYS AS IDENTITY,         /*ID*/

  kategoria_id BIGINT NOT NULL,                                 /*Kulso kulcs kategoria_id*/
  hely_azonosito VARCHAR(30) NOT NULL,                          /*egyedi kulcs*/
  aktiv BOOLEAN NOT NULL DEFAULT TRUE,                          /*Aktiv e 0/1*/


  /*Aktiv mezovel ideiglenesen kikapcsolhato anelkul hogy torolni keljen*/

  CONSTRAINT pk_parkolohely
    PRIMARY KEY (parkolohely_id),

  CONSTRAINT uq_parkolohely_azonosito
    UNIQUE (hely_azonosito),

  CONSTRAINT uq_parkolohely_id_kategoria
    UNIQUE (parkolohely_id, kategoria_id),

  CONSTRAINT fk_parkolohely_kategoria
    FOREIGN KEY (kategoria_id)
    REFERENCES kategoria (kategoria_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT                                /*Torles restrict*/
);
