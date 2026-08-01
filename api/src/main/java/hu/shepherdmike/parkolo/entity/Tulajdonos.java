/*   ____  _                _                  _ __  __ _ _
    / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
    \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
     ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
    |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                     |_|
*/

package hu.shepherdmike.parkolo.entity;


// ugyanazon jakarta anotaciok hasznalata
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;



@Entity
@Table(name = "tulajdonos")
public class Tulajdonos {

  @Id     // ID
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tulajdonos_id")

  private Long id;



  @Column(name= "keresztnev", nullable =false, length =100)
  private String keresztnev;


  @Column(name ="csaladnev", nullable = false, length =100)
  private String csaladnev;




  @Column(name = "specialis_status_id")       // tegyuk fel fogyatekkal elo, vagy anyuka etc.. Jovo implementaciora
  private Long specialisStatusId;

  protected Tulajdonos() {
  }


  public Tulajdonos(
    String keresztnev,
    String csaladnev,
    Long specialisStatusId
    ) 
  {
    this.keresztnev = keresztnev;
    this.csaladnev = csaladnev;
    this.specialisStatusId = specialisStatusId;
  }



//GETTEEEEEERS
  public Long getId() {
    return id;
  }


  public String getKeresztnev() {
    return keresztnev;
  
  }



  public String getCsaladnev() {
    return csaladnev;
  }


  public Long getSpecialisStatusId() {
    return specialisStatusId;
  }


  public String getTeljesNev() {
    return csaladnev +" " + keresztnev;
  }



}




