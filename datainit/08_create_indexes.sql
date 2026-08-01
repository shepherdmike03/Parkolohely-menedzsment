/*  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/

CREATE INDEX idx_tulajdonos_specialis_status
  ON tulajdonos (specialis_status_id);

CREATE INDEX idx_jarmu_tulajdonos
  ON jarmu (tulajdonos_id);

CREATE INDEX idx_jarmu_kategoria
  ON jarmu (kategoria_id);

CREATE INDEX idx_parkolohely_kategoria
  ON parkolohely (kategoria_id);

CREATE INDEX idx_tiltolista_tulajdonos
  ON tiltolista (tulajdonos_id);

CREATE INDEX idx_foglalas_jarmu
  ON foglalas (jarmu_id);

CREATE INDEX idx_foglalas_parkolohely
  ON foglalas (parkolohely_id);

CREATE INDEX idx_foglalas_kategoria
  ON foglalas (kategoria_id);

CREATE INDEX idx_foglalas_idoszak
  ON foglalas (kezdet_ido, veg_ido);




