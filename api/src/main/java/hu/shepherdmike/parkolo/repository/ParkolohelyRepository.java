//  ____  _                _                  _ __  __ _ _
// / ___|| |__   ___ _ __ | |__   ___ _ __ __| |  \/  (_) | _____
// \___ \| '_ \ / _ \ '_ \| '_ \ / _ \ '__/ _` | |\/| | | |/ / _ \
//  ___) | | | |  __/ |_) | | | |  __/ | | (_| | |  | | |   <  __/
// |____/|_| |_|\___| .__/|_| |_|\___|_|  \__,_|_|  |_|_|_|\_\___|
//                  |_|

package hu.shepherdmike.parkolo.repository;


import hu.shepherdmike.parkolo.entity.Parkolohely;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.List;




public interface ParkolohelyRepository
    extends JpaRepository<Parkolohely, Long> {

    List<Parkolohely> findAllByOrderByIdAsc();    // findAllByOrderByIdAsc
}
