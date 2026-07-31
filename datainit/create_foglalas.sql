/*  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/



CREATE TABLE foglalas (
  foglalas_id BIGINT GENERATED ALWAYS AS IDENTITY,        /*ID*/

  jarmu_id BIGINT NOT NULL,                   /* FK */
  kategoria_id BIGINT NOT NULL,               /*FK*/
  parkolohely_id BIGINT NOT NULL,             /*FK*/

  kezdet_ido TIMESTAMPTZ NOT NULL,
  veg_ido TIMESTAMPTZ NOT NULL,

  erkezes_ido TIMESTAMPTZ,
  elhagyas_ido TIMESTAMPTZ,

  letrehozva TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,      /*Jelen ido*/


  CONSTRAINT pk_foglalas
    PRIMARY KEY (foglalas_id),



  CONSTRAINT fk_foglalas_jarmu_kategoria    /*korlatozasok kulso kulcsra jarmu kategoria*/
    FOREIGN KEY (jarmu_id, kategoria_id)
    REFERENCES jarmu (jarmu_id, kategoria_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,   



  CONSTRAINT fk_foglalas_parkolohely_kategoria    /* korlatok parkolohely kategoria*/
    FOREIGN KEY (parkolohely_id, kategoria_id)
    REFERENCES parkolohely (parkolohely_id, kategoria_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,



  CONSTRAINT chk_foglalas_idotartam
    CHECK (veg_ido > kezdet_ido),



  CONSTRAINT chk_erkezes_ido
    CHECK (
        erkezes_ido IS NULL
        OR erkezes_ido >= kezdet_ido
    ),



  CONSTRAINT chk_elhagyas_ido
    CHECK (
        elhagyas_ido IS NULL
        OR erkezes_ido IS NOT NULL
    ),



  CONSTRAINT chk_erkezes_elhagyas
    CHECK (
        elhagyas_ido IS NULL
        OR elhagyas_ido >= erkezes_ido
    )
);
