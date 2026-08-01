/*
  ____  _                _                  _ __  __ _ _                     _    ___ 
 / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____     _       / \  |_ _|
 \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \  _| |_    / _ \  | | 
  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/ |_   _|  / ___ \ | | 
 |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|   |_|   /_/   \_\___|
                  |_|                                                                 
*/



package hu.shepherdmike.parkolo.dto;


public record NewCustomerVehicleResponse(

    Long tulajdonosId,
    String tulajdonosNev,

    Long jarmuId,
    String rendszam,

    Long kategoriaId,
    String kategoriaNev

) {
}
