
package batplayer;

import static batplayer.BatPlayer.myControllerHandle;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.util.Duration;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.text.html.ImageView;

/**
 *
 * @author gurpreet9001
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    public HBox hbox;
    
    public static MediaPlayer mediaPlayer;
    
    @FXML
    private MediaView mediaView;
    
    private String filePath;
   
    @FXML
    public Slider slider;
    
    @FXML
    public Slider seekslider;
    
    @FXML
    public Label playTime;
    
    @FXML
    private Button playlistid;
    
     @FXML
    private StackPane sp;
    
    void playmyvideo(String filePath){
        
        
      Media media =new Media(filePath);
       mediaPlayer =new MediaPlayer(media);
       mediaView.setMediaPlayer(mediaPlayer);
       
       // automatic fit to screen
       DoubleProperty width=mediaView.fitWidthProperty();
       DoubleProperty height=mediaView.fitHeightProperty();
       width.bind(Bindings.selectDouble(mediaView.sceneProperty(),"width"));
       height.bind(Bindings.selectDouble(mediaView.sceneProperty(),"height"));
       
       
       // volume slider get and set
       slider.setValue(mediaPlayer.getVolume()*100);
       slider.valueProperty().addListener(new InvalidationListener() {
           @Override
           public void invalidated(Observable observable) {
               mediaPlayer.setVolume(slider.getValue()/100);
           }
       });
       
       // operations on full screen 
       Stage stage = (Stage) mediaView.getScene().getWindow();
       mediaView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent doubleClicked) {
                 if(doubleClicked.getClickCount()==2 && stage.isFullScreen()==true){
                hbox.setVisible(true);
                seekslider.setVisible(true);
               
                 }
          
            else if(doubleClicked.getClickCount()==2 && stage.isFullScreen()==false){
                hbox.setVisible(false);
            seekslider.setVisible(false);
           
            
            // show controls on touching bottom of screen
            mediaView.setOnMouseExited(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
              hbox.setVisible(true);
                seekslider.setVisible(true);
               }
               
            });
            
            
            // on full screen hide the controls if mouse moved to normal screen and wait 2 seconds before disappering
            mediaView.setOnMouseEntered(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
               
 
Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                
      
                             hbox.setVisible(false);
                seekslider.setVisible(false);
           
            }
        });
        new Thread(sleeper).start();
    }  
            });
            
            }
            }
        });
       
      seekslider.maxProperty().bind(Bindings.createDoubleBinding(() -> mediaPlayer.getTotalDuration().toSeconds(),
    mediaPlayer.totalDurationProperty()));
       
     //changing value of seekslider by mouse  
       seekslider.setOnMouseClicked(new EventHandler<MouseEvent>() {
           @Override
           public void handle(MouseEvent event) {
              mediaPlayer.seek(Duration.seconds(seekslider.getValue()));
           }
       });
       
       mediaPlayer.play();
       
   // for moving seekslider
       mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
           @Override
           public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                if(seekslider.isPressed())
                    newValue=Duration.seconds(seekslider.getValue());
               seekslider.setValue(newValue.toSeconds());
              playTime.setText(formatTime(newValue, mediaPlayer.getMedia().getDuration()));
           }
       });
       
       
       //go to next video on end of first video
       mediaPlayer.setOnEndOfMedia(() -> {
           if(i<PlaylistFXMLController.arrlist.size()-1){
                i++;
                playmyvideo(PlaylistFXMLController.arrlist.get(i));}
            }); 
       
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
       
        // choose file to play through file explorer
       FileChooser fc=new FileChooser();
       FileChooser.ExtensionFilter filter=new FileChooser.ExtensionFilter("Select file(.mp4),(.mp3)","*.mp4", "*.mp3");
       fc.getExtensionFilters().add(filter);
       File file=fc.showOpenDialog(null);
       filePath =file.toURI().toString();
       
       if(filePath !=null){
         playmyvideo(filePath);
       }
    }
    
    @FXML
    private void keyPressed(KeyEvent event) {
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
    
     @FXML
            public void dragcame(DragEvent event) {
                if (event.getDragboard().hasFiles()) {
                    /*
                     * allow for both copying and moving, whatever user chooses
                     */
                    event.acceptTransferModes(TransferMode.COPY);
                }
                event.consume();
                
            }
    
     @FXML
            public void dragdropped(DragEvent event) {
               Dragboard db = event.getDragboard();
               List<String> validExtensions = Arrays.asList("mp4", "mp3");
               if (event.getDragboard().hasFiles()) {
                    // files on the dragboard must have an proper extension
                    if (!validExtensions.containsAll(
                            event.getDragboard().getFiles().stream()
                                    .map(file -> getExtension(file.getName()))
                                    .collect(Collectors.toList()))) {

                        event.consume();
                        return;
                    }

                    List<File> files = (ArrayList<File>) db.getContent(DataFormat.FILES);

                if (files != null) {
                    File file = files.get(0);
                     filePath =file.toURI().toString();
                
                    playmyvideo(filePath);
                }
                event.consume();
                }
                
                
                
            }

            // helping function to get extension of a file
    private String getExtension(String fileName){
        String extension = "";

        int j = fileName.lastIndexOf('.');
        if (j > 0 && j < fileName.length() - 1) //if the name is not empty
            return fileName.substring(j + 1).toLowerCase();

        return extension;
    }
    
    
    @FXML
    private void pauseVideo(ActionEvent event){
        mediaPlayer.pause();
    }
    @FXML
    private void playVideo(ActionEvent event){
        mediaPlayer.play();
        mediaPlayer.setRate(1);
    }
    @FXML
    private void stopVideo(ActionEvent event){
        mediaPlayer.stop();
    }
    @FXML
    private void fasterVideo(ActionEvent event){
         mediaPlayer.setRate(mediaPlayer.getRate() + 1);
    }
    @FXML
    private void slowerVideo(ActionEvent event){
        mediaPlayer.setRate(mediaPlayer.getRate() - 1);
    }
    @FXML
    private void screenshot(ActionEvent event){
  
        
         WritableImage writableImage = mediaView.snapshot(new SnapshotParameters(), null);
 
            File file = new File("capturedRoot.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
                System.out.println("Captured: " + file.getAbsolutePath());
            } catch (IOException ex) {
                
            }
    }
  
    @FXML
    private void makeplaylist(ActionEvent event) throws IOException{
         
         PlaylistFXMLController.arrlist.clear();
        
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PlaylistFXML.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));  
        stage.show();
stage.setTitle("Make Playlist");
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("bat.png")));
        
       // get a handle to the stage
       Stage stage1 = (Stage) playlistid.getScene().getWindow();
       // do what you have to do
       stage1.close();
       mediaPlayer.stop();
    }
    
    int i=0;
    
      @FXML
    private void next(ActionEvent event){
        mediaPlayer.stop();
        
        if(i<PlaylistFXMLController.arrlist.size()-1)
        i++;
        
        filePath =PlaylistFXMLController.arrlist.get(i);
                
      playmyvideo(filePath);
       
    }
    
    @FXML
    private void previous(ActionEvent event){
        mediaPlayer.stop();
        
        if(i>0)
        i--;
        
        filePath =PlaylistFXMLController.arrlist.get(i);
                
       playmyvideo(filePath);
       
    }
    
   
   
    
    private static String formatTime(Duration elapsed, Duration duration) {
   int intElapsed = (int)Math.floor(elapsed.toSeconds());
   int elapsedHours = intElapsed / (60 * 60);
   if (elapsedHours > 0) {
       intElapsed -= elapsedHours * 60 * 60;
   }
   int elapsedMinutes = intElapsed / 60;
   int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 
                           - elapsedMinutes * 60;
 
   if (duration.greaterThan(Duration.ZERO)) {
      int intDuration = (int)Math.floor(duration.toSeconds());
      int durationHours = intDuration / (60 * 60);
      if (durationHours > 0) {
         intDuration -= durationHours * 60 * 60;
      }
      int durationMinutes = intDuration / 60;
      int durationSeconds = intDuration - durationHours * 60 * 60 - 
          durationMinutes * 60;
      if (durationHours > 0) {
         return String.format("%d:%02d:%02d/%d:%02d:%02d", 
            elapsedHours, elapsedMinutes, elapsedSeconds,
            durationHours, durationMinutes, durationSeconds);
      } else {
          return String.format("%02d:%02d/%02d:%02d",
            elapsedMinutes, elapsedSeconds,durationMinutes, 
                durationSeconds);
      }
      } else {
          if (elapsedHours > 0) {
             return String.format("%d:%02d:%02d", elapsedHours, 
                    elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",elapsedMinutes, 
                    elapsedSeconds);
            }
        }
    }
     
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
                    
    }    
    
    
    
}
