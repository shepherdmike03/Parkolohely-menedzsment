/*
  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/




package hu.shepherdmike.parkolo.entity;


//jakarta anotaciok kellenek (Column, Id, Table, GeneratedValue, GenerationType, Entity)

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;



@Entity
@Table(name = "kategoria")      // a tabla amivel dolgozunk


public class Kategoria{


                    //ID
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "kategoria_id")
  
  private Long id;



            // A kategoria nev (Motorbicikli, Kamion, gepjarmu)
  @Column(name="kategoria_nev",nullable = false, unique = false)
  
  private String nev;



  protected Kategoria() {}
  
        // lekerdezzuk az id-t
  public Long getId() {
    return id;
  }



        // lekerdezzuk a nevet
  public String getNev() {
    return nev;
  }


        // megadhatunk egy nevet
  public void setNev(String nev) {
    this.nev = nev;
  }

}
