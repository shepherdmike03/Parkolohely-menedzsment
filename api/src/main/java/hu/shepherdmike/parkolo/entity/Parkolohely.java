//  ____  _                _                  _ __  __ _ _
// / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
// \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
//  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
// |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
//                  |_|




package hu.shepherdmike.parkolo.entity;




/*Mint a kategorianal, itt is szuksegunk van a COLUMN, Entity etc.. nevekre a jakarta konyvtarbol*/
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;




@Entity
@Table(name = "parkolohely")
public class Parkolohely {

    // ID 
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "parkolohely_id")
  
  private Long id;

    // azonosito
  @Column(name = "hely_azonosito", nullable = false, unique = true)
  
  private String helyAzonosito;


    // aktiv status
  @Column(name = "aktiv", nullable = false)
  private boolean aktiv = true;


      // joinolom a 2 tablat
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "kategoria_id", nullable = false)
  private Kategoria kategoria;



    // konstruktor
  public Parkolohely(
      String helyAzonosito,
      boolean aktiv,
      Kategoria kategoria
      ) 
  {
    this.helyAzonosito = helyAzonosito;
    this.aktiv=aktiv;
    this.kategoria=kategoria;
  }
  protected Parkolohely() {}




/*GETTEREK*/

  public Long getId() {
    return id;
  }



  public String getHelyAzonosito() {
    return helyAzonosito;
  }



  public boolean isAktiv() {
    return aktiv;
  }



  public Kategoria getKategoria(){
    return kategoria;
  }




}
