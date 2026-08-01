/*  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/



package hu.shepherdmike.parkolo.entity;




/*Anotaciok*/
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
@Table(name = "jarmu")

public class Jarmu {



  //ID
  @Id
  @GeneratedValue(strategy =GenerationType.IDENTITY)
  @Column(name= "jarmu_id")
  
  private Long id;



  @ManyToOne(fetch= FetchType.LAZY, optional=false)
  @JoinColumn(name ="tulajdonos_id", nullable= false)
  private Tulajdonos tulajdonos;




  @ManyToOne(fetch =FetchType.LAZY,optional =false)
  @JoinColumn(name= "kategoria_id", nullable = false)

  private Kategoria kategoria;



  @Column(name = "rendszam",nullable =false, unique= true, length = 20)
  private String rendszam;


//GETTEREK 

  protected Jarmu() {
  }

  public Jarmu(
    Tulajdonos tulajdonos,
    Kategoria kategoria,
    String rendszam
    )  
  {
    this.tulajdonos = tulajdonos;
    this.kategoria = kategoria;
    this.rendszam = rendszam;
  }

  public Long getId() {
    return id;
  }


  public Tulajdonos getTulajdonos() {
    return tulajdonos;
  }


  public Kategoria getKategoria() {
    return kategoria;
  }


  public String getRendszam() {
    return rendszam;
  }


}




