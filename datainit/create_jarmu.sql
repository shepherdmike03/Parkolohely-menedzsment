/*  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/


CREATE TABLE jarmu (
  jarmu_id BIGINT GENERATED ALWAYS AS IDENTITY,         /*ID*/

  tulajdonos_id BIGINT NOT NULL,                        /*Kulso Kulcs tulajdonos*/
  kategoria_id BIGINT NOT NULL,                         /*Kulso Kulcs  kategoria*/
  rendszam VARCHAR(20) NOT NULL,                        /*egyedi kulcs*/

  CONSTRAINT pk_jarmu
    PRIMARY KEY (jarmu_id),


  CONSTRAINT uq_jarmu_rendszam                /*egyedi legyen a rendszam*/
    UNIQUE (rendszam),


  CONSTRAINT uq_jarmu_id_kategoria            /*pk*/
    UNIQUE (jarmu_id, kategoria_id),



  CONSTRAINT fk_jarmu_tulajdonos            /*pk*/
    FOREIGN KEY (tulajdonos_id)
    REFERENCES tulajdonos (tulajdonos_id)
    ON UPDATE CASCADE         
    ON DELETE RESTRICT,                   /*Megakadajozza a tulajdonos torleset amig egy jarmu tartozik hozza*/


  CONSTRAINT fk_jarmu_kategoria
    FOREIGN KEY (kategoria_id)
    REFERENCES kategoria (kategoria_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);
