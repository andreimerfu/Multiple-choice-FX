import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class ChestionarController {

    @FXML private Label timeLabel;
    @FXML private JFXButton sendAnswer;
    @FXML private JFXButton exitButton;

    @FXML private Label quizText;
    @FXML private Label gresiteText;
    @FXML private Label corecteText;

    @FXML private JFXRadioButton answerA;
    @FXML private JFXRadioButton answerB;
    @FXML private JFXRadioButton answerC;

    @FXML private ImageView imagineQuiz;

    private String answer  = "";
    private Integer correctAnswers = 0;
    private Integer wrongAnswers = 0;
    private int i = -1;

    public static Integer idQuiz ;

    private final ArrayList<Integer> list = new ArrayList<Integer>();


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

    private void generateQuestions(){
        //Creaza o lista cu idQuiz-urile din baza de date, iar apoi face un
        //shuffle pentru a le amesteca astfel avem chestionare diferite mereu
        //O chestie buna ar fi ca numarul 100 sa fie inlocuit cu count intrebari
        //Din sql.
        DBConnect connect = new DBConnect();
        if(list.isEmpty()) {
            for (int i = 1; i <= connect.getCountFromSQL("questions"); i++) {
                list.add(new Integer(i));
            }

            Collections.shuffle(list);
        }
        if(i <= 25) {
            idQuiz = list.get(++i);
        }
    }

    @FXML private void initialize() throws InterruptedException {
        //Aici trebuie sa initializam o intrebare , apoi cand dai click pe buton;
        DBConnect connect = new DBConnect();
        generateQuestions();
       /* Aici deselectam radio butoanele si stegem imaignea care a fost inainte in imageView. In cazul in care
                vine o intrebare fara o imagine si inainte a fost una cu imagine , imaginea trebuie sa dispara
                in plus radiobutoanele nu trebuie sa ramana selectate.*/
        answerA.setSelected(false);
        answerB.setSelected(false);
        answerC.setSelected(false);
        imagineQuiz.setImage(null);
        quizText.setText(connect.getInfoFromQuestions("intrebareText",idQuiz));
        answerA.setText(connect.getInfoFromQuestions("varianta1Text",idQuiz));
        answerB.setText(connect.getInfoFromQuestions("varianta2Text",idQuiz));
        answerC.setText(connect.getInfoFromQuestions("varianta3Text",idQuiz));
        try{
            connect.getImageFromSQL(idQuiz,imagineQuiz);
        }catch (Exception ex){ System.out.println("Nu exista imagine."); }


    }

    @FXML private void handleButtonAction(ActionEvent event) throws IOException ,InterruptedException{
        if (event.getSource() == sendAnswer) {  //Apasam pe buton , verificam ce am bifat
            checkAnswer();  //Verificam ce am bifat si adaugam in string-ul answer , asta o sa il verfiicam cu raspunsu din baza de date
            DBConnect connect = new DBConnect();
            if (connect.verifyAnswer(idQuiz, answer)==true) {  //Verificam daca ce am introdus este corect;
                correctAnswers++;
                corecteText.setText("Intrebari corecte: "+correctAnswers);
            } else {
                wrongAnswers++;
                gresiteText.setText("Intrebari gresite: "+wrongAnswers);
            }
            answer = "";
            if(correctAnswers+wrongAnswers<26){
                //Daca nu s-au pus 26 de intrebari mai punem una
                initialize();
            }
            else if(correctAnswers+wrongAnswers==26){

            }
        }
    }
}