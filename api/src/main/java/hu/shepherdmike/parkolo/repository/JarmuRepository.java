/*
  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/



package hu.shepherdmike.parkolo.repository;

// JARMU
import hu.shepherdmike.parkolo.entity.Jarmu;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;


  //interfesz
public interface JarmuRepository
  extends JpaRepository<Jarmu,Long> {
  
  /*HELYES LEKERDEZES*/
  @Query("""
    SELECT j
    FROM Jarmu j
    JOIN FETCH j.tulajdonos
    JOIN FETCH j.kategoria
    WHERE j.id = :jarmuId
  """)
    
    Optional<Jarmu>findDetailedById(
        @Param("jarmuId") Long jarmuId
        );


  // reszletes
  @Query("""
  SELECT j
  FROM Jarmu j
  JOIN FETCH j.tulajdonos
  JOIN FETCH j.kategoria
  ORDER BY j.id ASC
  """)
    List<Jarmu> findAllDetailed();


  boolean existsByRendszamIgnoreCase(String rendszam);
}



