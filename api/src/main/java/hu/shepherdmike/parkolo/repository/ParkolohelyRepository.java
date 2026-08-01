//  ____  _                _                  _ __  __ _ _
// / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
// \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
//  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
// |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
//                  |_|

package hu.shepherdmike.parkolo.repository;


import hu.shepherdmike.parkolo.entity.Parkolohely;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;





public interface ParkolohelyRepository
    extends JpaRepository<Parkolohely, Long> {

    List<Parkolohely> findAllByOrderByIdAsc();    // findAllByOrderByIdAsc
    
    long countByAktivTrue();  /*AKTIV parkolohelyek darabszamja*/


  /*Talald meg a leg optimalisabb parkolohelyet*/
  @Query(
  value = """
    SELECT p.*
    FROM parkolohely p
    JOIN kategoria k
    ON k.kategoria_id= p.kategoria_id
    WHERE p.aktiv =TRUE
      AND k.meret_sorrend>=:minimumMeret
      AND NOT EXISTS (
        SELECT 1
        FROM foglalas f
        WHERE f.parkolohely_id = p.parkolohely_id
          AND f.kezdet_ido <:vegIdo
          AND f.veg_ido >:kezdetIdo
      )
    ORDER BY
    k.meret_sorrend ASC,
    p.parkolohely_id ASC
    LIMIT 1
    FOR UPDATE OF p SKIP LOCKED
  """,
  nativeQuery = true
    )
    

    Optional<Parkolohely> findBestAvailableSpotForUpdate(
        @Param("minimumMeret") Integer minimumMeret,
        @Param("kezdetIdo") OffsetDateTime kezdetIdo,
        @Param("vegIdo") OffsetDateTime vegIdo
    );




  // Szabad helyek listaja
  @Query("""
    SELECT p
    FROM Parkolohely p
    JOIN FETCH p.kategoria k
    WHERE p.aktiv =true
      AND NOT EXISTS (
        SELECT f.id
        FROM Foglalas f
        WHERE f.parkolohely =p
          AND f.kezdetIdo< :vegIdo
          AND f.vegIdo > :kezdetIdo
      )
    ORDER BY k.meretSorrend ASC, p.id ASC
  """)
    

    List<Parkolohely> findFreeSpots(
        @Param("kezdetIdo") OffsetDateTime kezdetIdo,
        @Param("vegIdo") OffsetDateTime vegIdo
        );




  /*Kompatibilis szabad helyek keresese*/
  @Query("""
    SELECT p
    FROM Parkolohely p
    JOIN FETCH p.kategoria k
    WHERE p.aktiv =true
      AND k.meretSorrend>= :minimumMeret
      AND NOT EXISTS (
        SELECT f.id
        FROM Foglalas f
        WHERE f.parkolohely= p
          AND f.kezdetIdo <:vegIdo
          AND f.vegIdo> :kezdetIdo
      )
    ORDER BY k.meretSorrend ASC, p.id ASC
  """)


    List<Parkolohely> findCompatibleFreeSpots(
        @Param("minimumMeret") Integer minimumMeret,
        @Param("kezdetIdo") OffsetDateTime kezdetIdo,
        @Param("vegIdo") OffsetDateTime vegIdo
        );


    // reszletes lekeres
  @Query("""
    SELECT p
    FROM Parkolohely p
    JOIN FETCH p.kategoria
    WHERE p.id = :parkolohelyId
  """)
  Optional<Parkolohely> findDetailedById(
      @Param("parkolohelyId") Long parkolohelyId
  );

}



