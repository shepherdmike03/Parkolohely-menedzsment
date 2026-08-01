/*  ____  _                _                  _ __  __ _ _
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
                  |_|
*/



package hu.shepherdmike.parkolo;

import org.springframework.boot.SpringApplication;   /*Felelos a spring boot alkalmazas elinditasaert*/

import org.springframework.boot.autoconfigure.SpringBootApplication;  // importalja a springboot anotaciokat.





@SpringBootApplication
public class ParkoloApp{
// a main metodus meghivja a ParkoloApp-ot
  public static void main(String[] args){
    SpringApplication.run(ParkoloApp.class,args)
  }
}

