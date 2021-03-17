/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package batplayer;

import static batplayer.BatPlayer.myControllerHandle;
import static batplayer.FXMLDocumentController.mediaPlayer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author gurpreet9001
 */
public class PlaylistFXMLController implements Initializable {

    
    private String filePath;
    private String filename;

     @FXML
    private Button playlistid;
     
       ObservableList<String> list = FXCollections.observableArrayList();            
            @FXML
            ListView<String> myplaylist ;
    
    public static ArrayList<String> arrlist = new ArrayList<String>(30);
    int i=0;
    
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
         
          
           //choose video for playlist through file explorer 
            @FXML
    private void openexplorer(ActionEvent event){
        FileChooser fc=new FileChooser();
       FileChooser.ExtensionFilter filter=new FileChooser.ExtensionFilter("Select file(.mp4),(.mp3)","*.mp4", "*.mp3");
       fc.getExtensionFilters().add(filter);
       File file=fc.showOpenDialog(null);
       filePath =file.toURI().toString();
       list.removeAll(list);
       filename=file.getName();
                     list.add(filename);
                     
                     arrlist.add(filePath);
                     myplaylist.getItems().addAll(list);
    }
            
           //choose video for playlist through drag and drop
          @FXML
            public void dragdropped(DragEvent event) {
               Dragboard db = event.getDragboard();
               List<String> validExtensions = Arrays.asList("mp4", "mp3");
               if (event.getDragboard().hasFiles()) {
                    // All files on the dragboard must have an accepted extension
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
                     filename=file.getName();
                     list.removeAll(list);
                     list.add(filename);
                     arrlist.add(filePath);
                     myplaylist.getItems().addAll(list);
                     
                }
                event.consume();
                } 
            }

            // Method to to get extension of a file
    private String getExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1) //if the name is not empty
            return fileName.substring(i + 1).toLowerCase();

        return extension;
    }         

    // submit the playlist and directly start playing the playlist
             @FXML
    private void submitplaylist(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
                        Parent root1 = loader.load();
 myControllerHandle = (FXMLDocumentController)loader.getController();
 
Stage stage = new Stage();
Scene scene1 = new Scene(root1);
stage.setScene(scene1);  
stage.show();
filePath =PlaylistFXMLController.arrlist.get(i);
       
//controller of main fxml controller
      myControllerHandle.playmyvideo(filePath);

        stage.setTitle("BatPlayer");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("bat.png")));
        
         scene1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent doubleClicked) {
                 if(doubleClicked.getClickCount()==2 && stage.isFullScreen()==true){
                stage.setFullScreen(false);}
          
            else if(doubleClicked.getClickCount()==2){
                stage.setFullScreen(true);}
            }
        });
        
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
                        if(myControllerHandle.mediaPlayer.getStatus()==MediaPlayer.Status.PLAYING)
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
        
// get a handle to the stage
 Stage stage1 = (Stage) playlistid.getScene().getWindow();
    // do what you have to do
    stage1.close();
                       
    }
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
