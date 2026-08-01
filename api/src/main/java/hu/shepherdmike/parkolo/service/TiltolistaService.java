/*
  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/

package hu.shepherdmike.parkolo.service;




import hu.shepherdmike.parkolo.dto.BlacklistPersonRequest;
import hu.shepherdmike.parkolo.dto.BlacklistPersonResponse;
import hu.shepherdmike.parkolo.entity.Tiltas;
import hu.shepherdmike.parkolo.entity.Tulajdonos;
import hu.shepherdmike.parkolo.exception.ApiConflictException;
import hu.shepherdmike.parkolo.exception.ResourceNotFoundException;
import hu.shepherdmike.parkolo.repository.TiltasRepository;
import hu.shepherdmike.parkolo.repository.TulajdonosRepository;


/*tranzakciok*/
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/*ido*/
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;



@Service
public class TiltolistaService {

  private final TulajdonosRepository tulajdonosRepository;
  private final TiltasRepository tiltasRepository;

  public TiltolistaService(
      TulajdonosRepository tulajdonosRepository,
      TiltasRepository tiltasRepository
      ) 
  {
    this.tulajdonosRepository = tulajdonosRepository;
    this.tiltasRepository = tiltasRepository;
  }





  /*Tiltas letrehozasa*/
  @Transactional
  public BlacklistPersonResponse blacklistPerson(
      BlacklistPersonRequest request
      ) 
  {
    Tulajdonos tulajdonos = tulajdonosRepository
      .findById(request.tulajdonosId())
      .orElseThrow(() -> new ResourceNotFoundException(
            "nem letezik ez az ID: "
            + request.tulajdonosId()
            ));

    OffsetDateTime most = OffsetDateTime.now(ZoneOffset.UTC);

    if (
        request.tiltasVege() != null
        && !request.tiltasVege().isAfter(most)
       ) 
    {
      throw new IllegalArgumentException(
          "a tiltas vege a jovoben legyen"
          );
    }

    List<Tiltas> aktivTiltasok =
      tiltasRepository.findActiveByOwnerId(
          tulajdonos.getId(),
          most
          );



    if (!aktivTiltasok.isEmpty()) {
      throw new ApiConflictException(
          "ez a szemely aktiv tiltas alatt al"
          + "ok: "
          + aktivTiltasok.getFirst().getTiltasOk()
          );
    }


    /*Tiltas letrehozasa*/
    Tiltas tiltas =new Tiltas(
        tulajdonos,
        request.ok().trim(),
        most,
        request.tiltasVege()
        );



    Tiltas mentettTiltas =tiltasRepository.save(tiltas);


    /*lista visszateritese*/
    return new BlacklistPersonResponse(
        mentettTiltas.getId(),
        tulajdonos.getId(),
        tulajdonos.getTeljesNev(),
        mentettTiltas.getTiltasOk(),
        mentettTiltas.getTiltasKezdete(),
        mentettTiltas.getTiltasVege(),
        mentettTiltas.isAktiv()
        );

  }
}



