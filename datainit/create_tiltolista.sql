/*  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/

CREATE TABLE tiltolista (
    tiltas_id BIGINT GENERATED ALWAYS AS IDENTITY,          /*ID*/
    
    tulajdonos_id BIGINT NOT NULL,                          /*KULSO KULCS*/
    tiltas_ok VARCHAR(500) NOT NULL,                        
    tiltas_kezdete TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tiltas_vege TIMESTAMPTZ,
    aktiv BOOLEAN NOT NULL DEFAULT TRUE,        /*Aktivval ki lehet szedni anelkul hogy toroljuk*/

    CONSTRAINT pk_tiltolista
        PRIMARY KEY (tiltas_id),



    CONSTRAINT fk_tiltolista_tulajdonos
        FOREIGN KEY (tulajdonos_id)
        REFERENCES tulajdonos (tulajdonos_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,              /*torles restrict*/



    CONSTRAINT chk_tiltas_idotartam       /*tiltas idotartam*/
        CHECK (
            tiltas_vege IS NULL
            OR tiltas_vege > tiltas_kezdete
        )
);
