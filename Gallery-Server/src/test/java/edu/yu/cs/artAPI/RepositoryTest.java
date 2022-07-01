// package edu.yu.cs.gallery;

// import edu.yu.cs.gallery.repositories.ArtRepository;
// import edu.yu.cs.gallery.repositories.GalleryRepository;

// import javax.inject.Inject;
// import javax.transaction.Transactional;

// import org.junit.jupiter.api.Test;

// import io.quarkus.test.junit.QuarkusTest;

// @QuarkusTest
// public class RepositoryTest {
 
//     @Inject
//     ArtRepository artRepository;
     
//     @Inject
//     GalleryRepository galleryRepository;
 
//     @Test
//     @Transactional
//     public void testPersistPanacheRepositoryPattern() {
//         Art art = new Art();
//         art.setName("mona lisa");
//         art.setCreator("mona lisa");
//         artRepository.persist(art);
//         // Gallery gallery = new Gallery();
//         // gallery.setName("Gallery1");

//         // Art art1 = artRepository.findById(1L);
//         // Art art2 = chessPlayerRepository.findById(2L);

//         // chessGame.setPlayerWhite(chessPlayer1);
//         // chessGame.setPlayerBlack(chessPlayer2);

//         // chessGameRepository.persist(chessGame);

//     }
// }