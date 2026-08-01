/*
  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/


package hu.shepherdmike.parkolo.entity;




// anotaciok
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;



/*ido Kezeles*/
import java.time.OffsetDateTime;
import java.time.ZoneOffset;




@Entity
@Table(name= "foglalas")
public class Foglalas {



  @Id     // ID
  @GeneratedValue(strategy =GenerationType.IDENTITY)
  @Column(name ="foglalas_id")

  private Long id;



        // JARMU join
  @ManyToOne(fetch =FetchType.LAZY, optional = false)
  @JoinColumn(name ="jarmu_id", nullable = false)

  private Jarmu jarmu;




        // PARKOLOHELY join
  @ManyToOne(fetch = FetchType.LAZY, optional =false)
  @JoinColumn(name= "parkolohely_id", nullable = false)

  private Parkolohely parkolohely;



  /*ATTRIBUTUMOK*/

  @Column(name="kezdet_ido",nullable = false)
  private OffsetDateTime kezdetIdo;


  @Column(name ="veg_ido",nullable = false)
  private OffsetDateTime vegIdo;


  @Column(name ="erkezes_ido")
  private OffsetDateTime erkezesIdo;


  @Column(name= "elhagyas_ido")
  private OffsetDateTime elhagyasIdo;


  @Column(name ="letrehozva",nullable = false)
  private OffsetDateTime letrehozva;


  protected Foglalas() {
  }




  // KONSTRUKTOR
   public Foglalas(
      Jarmu jarmu,
      Parkolohely parkolohely,
      OffsetDateTime kezdetIdo,
      OffsetDateTime vegIdo
  ) {
    this(
        jarmu,
        parkolohely,
        kezdetIdo,
        vegIdo,
        OffsetDateTime.now(ZoneOffset.UTC)
    );
  }




  public Foglalas(
      Jarmu jarmu,
      Parkolohely parkolohely,
      OffsetDateTime kezdetIdo,
      OffsetDateTime vegIdo,
      OffsetDateTime letrehozva
  ) {
    this.jarmu = jarmu;
    this.parkolohely = parkolohely;
    this.kezdetIdo = kezdetIdo;
    this.vegIdo = vegIdo;
    this.letrehozva = letrehozva;
  }




  /*GETTEREEEK*/
  public Long getId() {
    return id;
  }



  public Jarmu getJarmu() {
    return jarmu;
  }



  public Parkolohely getParkolohely() {
    return parkolohely;
  }



  public OffsetDateTime getKezdetIdo() {
    return kezdetIdo;
  }



  public OffsetDateTime getVegIdo() {
    return vegIdo;
  }



  public OffsetDateTime getErkezesIdo() {
    return erkezesIdo;
  }



  public OffsetDateTime getElhagyasIdo() {
    return elhagyasIdo;
  }



  public OffsetDateTime getLetrehozva() {
    return letrehozva;
  }


}


