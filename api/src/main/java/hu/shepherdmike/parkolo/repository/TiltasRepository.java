/*
  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/




package hu.shepherdmike.parkolo.repository;

import hu.shepherdmike.parkolo.entity.Tiltas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


// ido
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;



public interface TiltasRepository
  extends JpaRepository<Tiltas,Long> {
      // tiltott uriemberek lekerdezese
  @Query("""
    SELECT t
    FROM Tiltas t
    JOIN FETCH t.tulajdonos
    WHERE t.tulajdonos.id = :tulajdonosId
      AND t.aktiv = true
      AND (
        t.tiltasVege IS NULL
        OR t.tiltasVege > :most
      )
    ORDER BY t.tiltasKezdete DESC
  """)



  List<Tiltas> findActiveByOwnerId(
      @Param("tulajdonosId") Long tulajdonosId,
      @Param("most") OffsetDateTime most
      );

  @Query("""
    SELECT t
    FROM Tiltas t
    JOIN FETCH t.tulajdonos
    ORDER BY t.id DESC
  """)
  List<Tiltas> findAllDetailed();


  @Query("""
    SELECT t
    FROM Tiltas t
    JOIN FETCH t.tulajdonos
    WHERE t.id = :tiltasId
  """)
  Optional<Tiltas> findDetailedById(
      @Param("tiltasId") Long tiltasId
  );

}




