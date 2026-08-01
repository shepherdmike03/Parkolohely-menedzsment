/*
  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/





package hu.shepherdmike.parkolo.repository;

import hu.shepherdmike.parkolo.entity.Foglalas;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// ido + utils
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;





public interface FoglalasRepository
  extends JpaRepository<Foglalas,Long> {


    /*Foglalasokat visszaterito querry*/
  @Query("""
  SELECT COUNT(f)
  FROM Foglalas f
  WHERE f.jarmu.id = :jarmuId
  AND f.kezdetIdo < :vegIdo
  AND f.vegIdo > :kezdetIdo
  """)


    long countVehicleOverlaps(
        @Param("jarmuId") Long jarmuId,
        @Param("kezdetIdo") OffsetDateTime kezdetIdo,
        @Param("vegIdo") OffsetDateTime vegIdo
        );



    //jelenleg aktiv foglalasok
  @Query("""
  SELECT COUNT(DISTINCT f.parkolohely.id)
  FROM Foglalas f
  WHERE f.parkolohely.aktiv = true
  AND f.kezdetIdo < :vegIdo
  AND f.vegIdo > :kezdetIdo
  """)


    long countOccupiedSpots(
        @Param("kezdetIdo") OffsetDateTime kezdetIdo,
        @Param("vegIdo") OffsetDateTime vegIdo
        );

  

    /*AZ osszes foglalas + minden reszlet*/
  @Query("""
  SELECT f
  FROM Foglalas f
  JOIN FETCH f.jarmu j
  JOIN FETCH j.tulajdonos
  JOIN FETCH j.kategoria
  JOIN FETCH f.parkolohely p
  JOIN FETCH p.kategoria
  ORDER BY f.letrehozva DESC, f.id DESC
  """)
  
    List<Foglalas> findAllDetailed();

  
    /*Bizonyos foglalasrol minden reszlet*/
  @Query("""
  SELECT f
  FROM Foglalas f
  JOIN FETCH f.jarmu j
  JOIN FETCH j.tulajdonos
  JOIN FETCH j.kategoria
  JOIN FETCH f.parkolohely p
  JOIN FETCH p.kategoria
  WHERE f.id = :foglalasId
  """)


    Optional<Foglalas> findDetailedById(
        @Param("foglalasId") Long foglalasId
        );



}



