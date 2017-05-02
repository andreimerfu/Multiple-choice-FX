import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXProgressBar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sun.rmi.runtime.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.util.*;


public class MediuInvatareController{

    @FXML private JFXButton sendAnswerButton;
    @FXML private JFXButton exitButton;
    @FXML private JFXButton jumpAnswerButton;
    @FXML private JFXProgressBar progressBar;
    @FXML private Text questionLabel;
    @FXML private JFXCheckBox answerA;
    @FXML private JFXCheckBox answerB;
    @FXML private JFXCheckBox answerC;
    @FXML private ImageView imageView;

    private Integer idQuiz ;
    private boolean isDeserializated = false;
    private Queue deserializedQueue ;
    private String answer = "";

    private ArrayList<Integer> questionsList = new ArrayList<Integer>();

    private void deserialize(){

        Connection conn = null;


        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            byte[] byteList = (byte[])DBConnect.readJavaObject(conn,LoginController.idAccount_Current);
            ObjectInputStream inStream = new ObjectInputStream(new ByteArrayInputStream(byteList));
            deserializedQueue = (Queue) inStream.readObject();

        }catch (Exception ex) { ex.printStackTrace(); };

    }

    private void checkAnswer() { //O mica functie care verifica ce buton ai apasat
        if (answerA.isSelected()) {
            answer+="a";
        }
        if (answerB.isSelected()) {
            answer+="b";
        }
        if (answerC.isSelected()) {
            answer+="c";
        }
    }


    public void start(Stage stage)throws IOException {
        Parent home = FXMLLoader.load(getClass().getResource("/MediuInvatare.fxml"));
        Scene homeScene = new Scene(home, 1000, 600);
        stage.setScene(homeScene);
        stage.show();
    }

    @FXML private void handleButtonAction(ActionEvent event) throws IOException ,InterruptedException{
        if(event.getSource() == exitButton){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirma");
            alert.setHeaderText("Chestionare Auto categoria B");
            alert.setContentText("Esti sigur ca vrei sa iesi din mediul de invatare?");
            alert.showAndWait().ifPresent(response -> {
                if(response == ButtonType.OK){
                    try {
                        Stage stage = (Stage) exitButton.getScene().getWindow();
                        stage.setTitle("Chestionare Auto categoria B");
                        HomeController home = new HomeController();
                        home.start(stage);
                    }catch (IOException ex){ ex.printStackTrace(); }
                }
                else{
                    alert.close();
                }
            });
        }

        if(event.getSource() == sendAnswerButton){
            DBConnect connect = new DBConnect();
            checkAnswer();
            if(connect.verifyAnswer(idQuiz, answer)){
                deserializedQueue.remove();
                initialize();
            }
            else{
                deserializedQueue.remove();
                deserializedQueue.add(idQuiz);
                initialize();
            }

        }

        if(event.getSource() == jumpAnswerButton){
            deserializedQueue.remove();
            deserializedQueue.add(idQuiz);
            initialize();
        }

        Connection connection = null;
        try{
            connection = DBConnect.getConnection();
            connection.setAutoCommit(false);

            DBConnect.writeJavaObject(connection,deserializedQueue);
            connection.commit();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML private void initialize() throws InterruptedException {

        DBConnect connect = new DBConnect();
        answer = "";

        if(!isDeserializated) {
            deserialize();
            isDeserializated = true;
        }

        if(!deserializedQueue.isEmpty())
            idQuiz = (int)deserializedQueue.element();

        /* Setam progressBarul prin raportul dintre atributul progresMediu caracteristic fiecarui account din baza de date si numarul total de intrebari
        Atunci cand progresMediu = numaru total de intrebari * 2 atunci progressBar se va seta cu 1 si o sa fie complet mediul de invatare. */

        progressBar.setProgress((double)((connect.getCountFromSQL("questions") * 2)-deserializedQueue.size())/(connect.getCountFromSQL("questions") * 2));
        if (progressBar.getProgress() == 1){
            //Daca progressBar este 1 am terminat mediul de invatare, generam altul
            for (int i = 1; i <= connect.getCountFromSQL("questions"); ++i) {
                questionsList.add(i);
            }

            for (int i = 1; i <= connect.getCountFromSQL("questions"); ++i) {
                questionsList.add(i);
            }

            Collections.shuffle(questionsList);

            for (int i = 0; i < questionsList.size(); ++i) {
                deserializedQueue.add(questionsList.get(i));
            }
            initialize();
        }

        answerA.setSelected(false);
        answerB.setSelected(false);
        answerC.setSelected(false);
        imageView.setImage(null);


        questionLabel.setText(connect.getInfoFromQuestions("intrebareText",idQuiz));
        answerA.setText(connect.getInfoFromQuestions("varianta1Text",idQuiz));
        answerB.setText(connect.getInfoFromQuestions("varianta2Text",idQuiz));
        answerC.setText(connect.getInfoFromQuestions("varianta3Text",idQuiz));

        try{
            connect.getImageFromSQL(idQuiz,imageView);
        }catch (Exception ex) { System.out.println(ex); }


    }

}
