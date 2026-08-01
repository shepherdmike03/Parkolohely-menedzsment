/*  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/

CREATE TABLE tulajdonos (
  tulajdonos_id BIGINT GENERATED ALWAYS AS IDENTITY,        /*ID*/

  keresztnev VARCHAR(100) NOT NULL,                         /*Keresztnev*/
  csaladnev VARCHAR(100) NOT NULL,                           /*CsaladNev*/
  specialis_status_id BIGINT,                              /*Am i special?l*/

  CONSTRAINT pk_tulajdonos
    PRIMARY KEY (tulajdonos_id),

  CONSTRAINT fk_tulajdonos_specialis_status
    FOREIGN KEY (specialis_status_id)                          /*Special kulso kulcs*/
    REFERENCES specialis_status (specialis_status_id)
    ON UPDATE CASCADE
    ON DELETE SET NULL
);
