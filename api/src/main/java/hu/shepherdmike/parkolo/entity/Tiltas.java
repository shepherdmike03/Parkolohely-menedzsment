/*____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/



package hu.shepherdmike.parkolo.entity;


/*annotaciok*/
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;



/*IDO kezeleshez*/
import java.time.OffsetDateTime;




@Entity
@Table(name = "tiltolista")
public class Tiltas {

  @Id       //ID
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name ="tiltas_id")
  private Long id;





  @ManyToOne(fetch = FetchType.LAZY,optional= false)
  @JoinColumn(name ="tulajdonos_id", nullable = false)
  private Tulajdonos tulajdonos;




  @Column(name ="tiltas_ok", nullable = false,length = 500)
  private String tiltasOk;                /*Cigizett / nem ment el idoben / nem fizetett etc..*/


/*BANN AND UNBANN*/
  @Column(name= "tiltas_kezdete",nullable = false)
  private OffsetDateTime tiltasKezdete;



  @Column(name = "tiltas_vege")
  private OffsetDateTime tiltasVege;


//JELENLEG LE VAN E Tiltva az uriember
  @Column(name = "aktiv", nullable = false)
  private boolean aktiv;



  protected Tiltas() {
  }



  // konstruktor
  public Tiltas(
      Tulajdonos tulajdonos,
      String tiltasOk,
      OffsetDateTime tiltasKezdete,
      OffsetDateTime tiltasVege
      ) 
  {
    this(
        tulajdonos,
        tiltasOk,
        tiltasKezdete,
        tiltasVege,
        true
        );
  }



  public Tiltas(
    Tulajdonos tulajdonos,
    String tiltasOk,
    OffsetDateTime tiltasKezdete,
    OffsetDateTime tiltasVege,
    boolean aktiv
    ) 
  {
    this.tulajdonos = tulajdonos;
    this.tiltasOk = tiltasOk;
    this.tiltasKezdete = tiltasKezdete;
    this.tiltasVege = tiltasVege;
    this.aktiv = aktiv;

  }

// GETTEREEEEEEEEK
  public Long getId() {
    return id;
  }



  public Tulajdonos getTulajdonos() {
    return tulajdonos;
  }


  public String getTiltasOk() {
    return tiltasOk;
  }


  public OffsetDateTime getTiltasKezdete() {
    return tiltasKezdete;
  }


  public OffsetDateTime getTiltasVege() {
    return tiltasVege;
  }


  public boolean isAktiv() {
    return aktiv;
  }



}


