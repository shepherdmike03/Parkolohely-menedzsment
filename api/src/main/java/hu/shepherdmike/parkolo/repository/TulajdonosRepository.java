/*
  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/





package hu.shepherdmike.parkolo.repository;


  // tulajdonos
import hu.shepherdmike.parkolo.entity.Tulajdonos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


  /*Interfesz*/
public interface TulajdonosRepository
    extends JpaRepository<Tulajdonos, Long> {

  List<Tulajdonos> findAllByOrderByIdAsc();
}


