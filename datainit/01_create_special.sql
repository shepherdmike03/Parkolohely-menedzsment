/*  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/



CREATE TABLE specialis_status (
  specialis_status_id BIGINT GENERATED ALWAYS AS IDENTITY,         /*ID*/
  specialis_status_nev VARCHAR(100) NOT NULL,                     /*Terhes, dolgozo, vagy fogyatekos*/


  CONSTRAINT pk_specialis_status
    PRIMARY KEY (specialis_status_id),          /*Primary Key*/

  CONSTRAINT uq_specialis_status_nev            /*egyedi nev*/
    UNIQUE (specialis_status_nev)
)
