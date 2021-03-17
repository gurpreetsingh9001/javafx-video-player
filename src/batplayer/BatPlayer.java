/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package batplayer;

import static batplayer.FXMLDocumentController.mediaPlayer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author gurpreet9001
 */
public class BatPlayer extends Application {

    
    
    static boolean isSplashLoaded=false;
    static FXMLDocumentController myControllerHandle;
    
    @Override
    public void start(Stage stage) throws Exception {
        
       // Load Splash screen first
        Parent pane = FXMLLoader.load(getClass().getResource(("SplashFXML.fxml")));
       Scene scene = new Scene(pane);
        stage.setScene(scene);
      stage.show();
        stage.setTitle("BatPlayer");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("bat.png")));
        
        //splash screen setup
        if (!BatPlayer.isSplashLoaded) {
               BatPlayer.isSplashLoaded = true;
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), pane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setCycleCount(1);

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), pane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);

            fadeIn.play();

            fadeIn.setOnFinished((e) -> {
                fadeOut.play();
            });

            // all operations start after splash is finished
            fadeOut.setOnFinished((e) -> {
               
                 try {
                     FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
                        Parent root = loader.load();
                Scene scene1 = new Scene(root);
                // get controller from FXMLDoucument to manipulate mediaplayer directly from any external file
                  myControllerHandle = (FXMLDocumentController)loader.getController();
                  
                  //get fullscreen or come out of it
        scene1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent doubleClicked) {
                 if(doubleClicked.getClickCount()==2 && stage.isFullScreen()==true){
                stage.setFullScreen(false);}
          
            else if(doubleClicked.getClickCount()==2){
                stage.setFullScreen(true);}
            }
        });
        
        //keylisteners
       scene1.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:
                        myControllerHandle.mediaPlayer.setVolume(myControllerHandle.mediaPlayer.getVolume() + 0.03);myControllerHandle.slider.setValue(mediaPlayer.getVolume()*100);break;
                    case DOWN:
                        myControllerHandle.mediaPlayer.setVolume(myControllerHandle.mediaPlayer.getVolume() - 0.03);myControllerHandle.slider.setValue(mediaPlayer.getVolume()*100);break;
                    case LEFT:
                        myControllerHandle.seekslider.setValue(Duration.seconds(myControllerHandle.seekslider.getValue()).toSeconds() - 5);mediaPlayer.seek(Duration.seconds(myControllerHandle.seekslider.getValue()));break;
                    case RIGHT:
                        myControllerHandle.seekslider.setValue(Duration.seconds(myControllerHandle.seekslider.getValue()).toSeconds() + 5);mediaPlayer.seek(Duration.seconds(myControllerHandle.seekslider.getValue()));break;
                    case M: 
                        myControllerHandle.mediaPlayer.setVolume(0);myControllerHandle.slider.setValue(mediaPlayer.getVolume()*100);break;
                    case SPACE:
                        if(myControllerHandle.mediaPlayer.getStatus()==Status.PLAYING)
                            myControllerHandle.mediaPlayer.pause();
                        else{
                            myControllerHandle.mediaPlayer.play();
                            myControllerHandle.mediaPlayer.setRate(1);
                        }
                        break;
                     case PLUS:if(myControllerHandle.mediaPlayer.getRate()==0.5)
                                   myControllerHandle.mediaPlayer.setRate(1); 
                            else
                        myControllerHandle.mediaPlayer.setRate(myControllerHandle.mediaPlayer.getRate() + 1);break;
                        
                    case EQUALS:if(mediaPlayer.getRate()>=1)
                        myControllerHandle.mediaPlayer.setRate(myControllerHandle.mediaPlayer.getRate() - 0.5);
                    else
                        myControllerHandle.mediaPlayer.setRate(0.5);
                        break;
                    case ADD:if(myControllerHandle.mediaPlayer.getRate()==0.5)
                                   myControllerHandle.mediaPlayer.setRate(1); 
                            else
                        myControllerHandle.mediaPlayer.setRate(myControllerHandle.mediaPlayer.getRate() + 0.5);
                    break;
                    case SUBTRACT:
                        if(myControllerHandle.mediaPlayer.getRate()>=1){
                        myControllerHandle.mediaPlayer.setRate(myControllerHandle.mediaPlayer.getRate() - 0.5);}
                    else
                        myControllerHandle.mediaPlayer.setRate(0.5);
                        break;
                }
            }
        });
        
                stage.setScene(scene1);
                stage.show();

                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
       
        
    }
    
  

    

   
    public static void main(String[] args) {
        
        launch(args);
    }
    
}
