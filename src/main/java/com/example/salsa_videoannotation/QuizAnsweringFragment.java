package com.example.salsa_videoannotation;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import static com.example.salsa_videoannotation.MainActivity.annotationWrapperList;
import static com.example.salsa_videoannotation.PlayerActivity.answersHashMap;
import static com.example.salsa_videoannotation.PlayerActivity.noQuizAnswersCompleted;
import static com.example.salsa_videoannotation.PlayerActivity.quizScore;

import java.util.List;

/**
 * Fragment inserted within the PlayerActivity class to answer Quiz Questions
 */
public class QuizAnsweringFragment extends Fragment {
    private TextView question;
    private Button answer1;
    private Button answer2;
    private Button answer3;
    private Button correctAnswer;
    private PlayerActivity playerActivity;
    private AnnotationWrapper currentAnnotationWrapper;
    private Annotations currentAnnotation;
    private View view;
    public static final int UNANSWERED = 0;
    public static final int ANSWER_CORRECT = 1;
    public static final int ANSWER_INCORRECT = 2;
    private static final int HINT_CATEGORY = 0;
    private static final int HINT_BODYPART = 1;
    public int nextAnnotationId;
    public int prevAnnotationId;


    public QuizAnsweringFragment() {
        // Required empty public constructor
    }


    /**
     * Inflates the layout for answering Quiz Questions
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_quiz_answering, container, false);
        question = view.findViewById(R.id.question_display);
        playerActivity = (PlayerActivity) getActivity();
        // Acquires the details from the Bundle created on the AnnotationDisplayAdapter class or PlayerActivity to populate
        // the fields for viewing and enabling onClick for next and previous arrows
        // Sets up buttons for answering the quiz questions
        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            String annotationWrapperId = bundle.getString("annotationWrapperId");
            int annotationId = bundle.getInt("annotationId");
            nextAnnotationId = bundle.getInt("nextAnnotationId");
            if(nextAnnotationId == -1)
            {
                ImageView nextAnnotation = view.findViewById(R.id.next_Annotation);
                nextAnnotation.setClickable(false);
                nextAnnotation.setEnabled(false);
            }
            prevAnnotationId = bundle.getInt("prevAnnotationId");
            if(prevAnnotationId == -1)
            {
                ImageView prevAnnotation = view.findViewById(R.id.prev_Annotation);
                prevAnnotation.setClickable(false);
                prevAnnotation.setEnabled(false);
            }
            currentAnnotationWrapper = annotationWrapperList.get(annotationWrapperId);
            currentAnnotation = currentAnnotationWrapper.getVideoAnnotationsMap().get(annotationId);
            // Decides if quiz question has already been answered or not based on the AnswerHashMap
            if(answersHashMap.get(currentAnnotation.getId()).getAnswerStatus() != UNANSWERED)
            {
                // Sets up an answered question
                QuizAnswers quizAnswers = answersHashMap.get(currentAnnotation.getId());
                setUpAnsweredButtons(quizAnswers);
            }
            else
            {
                // Sets up an unanswered question
                assignButtonsToAnswers();
                setButtonText();
                setOnclickListeners();
            }
            // Moves the video player to the Annotation's position
            playerActivity.simpleExoPlayer.seekTo(currentAnnotation.getStartTime());
            // Pauses playback
            playerActivity.simpleExoPlayer.setPlayWhenReady(false);
        }
        return view;
    }

    /**
     * Sets up the buttons for an Answered Question
     * @param quizAnswers
     */
    private void setUpAnsweredButtons(QuizAnswers quizAnswers)
    {
        // Sets the correct answer to the top left button position
        correctAnswer = view.findViewById(R.id.answer_button_1);
        answer1 = view.findViewById(R.id.answer_button_2);
        answer2 = view.findViewById(R.id.answer_button_3);
        answer3 = view.findViewById(R.id.answer_button_4);
        // Method to set the button text
        setButtonText();
        // Sets up buttons as to whether the question was answered correctly or incorrectly
        if(quizAnswers.getAnswerStatus() == ANSWER_CORRECT)
        {
            doCorrectAnswerButtonChanges();
        }
        else
        {
            // Checks which incorrect answer was selected as the answer
            if(answer1.getText().toString().equals(quizAnswers.getAnswerText()))
                doIncorrectAnswerButtonChanges(answer1);
            else if(answer2.getText().toString().equals(quizAnswers.getAnswerText()))
                doIncorrectAnswerButtonChanges(answer2);
            else if(answer3.getText().toString().equals(quizAnswers.getAnswerText()))
                doIncorrectAnswerButtonChanges(answer3);
        }
        // Makes all buttons not clickable
        correctAnswer.setClickable(false);
        answer1.setClickable(false);
        answer2.setClickable(false);
        answer3.setClickable(false);
    }

    /**
     * Randomly assigns an answer to a button
     */
    public void assignButtonsToAnswers()
    {
        // Generates a random number to decide which configuration to use
        int randomNum = (int)(Math.random()*(4-1+1) + 1);
        switch (randomNum)
        {
            // Assigns a button variable to a random button object on the GUI
            case 1:
                correctAnswer = view.findViewById(R.id.answer_button_1);
                answer1 = view.findViewById(R.id.answer_button_2);
                answer2 = view.findViewById(R.id.answer_button_3);
                answer3 = view.findViewById(R.id.answer_button_4);
                break;
            case 2:
                correctAnswer = view.findViewById(R.id.answer_button_2);
                answer1 = view.findViewById(R.id.answer_button_1);
                answer2 = view.findViewById(R.id.answer_button_3);
                answer3 = view.findViewById(R.id.answer_button_4);
                break;
            case 3:
                correctAnswer = view.findViewById(R.id.answer_button_3);
                answer1 = view.findViewById(R.id.answer_button_2);
                answer2 = view.findViewById(R.id.answer_button_1);
                answer3 = view.findViewById(R.id.answer_button_4);
                break;
            case 4:
                correctAnswer = view.findViewById(R.id.answer_button_4);
                answer1 = view.findViewById(R.id.answer_button_2);
                answer2 = view.findViewById(R.id.answer_button_3);
                answer3 = view.findViewById(R.id.answer_button_1);
                break;
        }
    }


    /**
     * Sets the text on the button variables
     */
    private void setButtonText()
    {
        question.setText(currentAnnotation.getQuizQuestion().getQuestion());
        answer1.setText(currentAnnotation.getQuizQuestion().getAnswer1());
        answer2.setText(currentAnnotation.getQuizQuestion().getAnswer2());
        answer3.setText(currentAnnotation.getQuizQuestion().getAnswer3());
        correctAnswer.setText(currentAnnotation.getQuizQuestion().getCorrectAnswer());
    }

    /**
     * Sets the onClickListeners for the answer buttons
     */
    private void setOnclickListeners()
    {
        correctAnswer.setOnClickListener(this::onCorrectAnswerClick);
        answer1.setOnClickListener(this::onIncorrectAnswerClick);
        answer2.setOnClickListener(this::onIncorrectAnswerClick);
        answer3.setOnClickListener(this::onIncorrectAnswerClick);
    }


    /**
     * Controls behaviour when the correct answer is selected
     * @param view
     */
    public void onCorrectAnswerClick(View view)
    {
        // method to change visible aspects of the button
        doCorrectAnswerButtonChanges();
        // Updates the answersHashMap with a correctly answered question variable
        QuizAnswers quizAnswers = answersHashMap.get(currentAnnotation.getId());
        quizAnswers.setAnswerText(correctAnswer.getText().toString());
        quizAnswers.setAnswerStatus(ANSWER_CORRECT);
        answersHashMap.replace(currentAnnotation.getId(), quizAnswers);
        // Increments quizScore variable
        quizScore += 1;
        // Increments number of answers completed variable
        noQuizAnswersCompleted +=1;
    }

    /**
     * Controls behaviour when the incorrect answer is selected
     * @param view
     */
    public void onIncorrectAnswerClick(View view)
    {
        // Updates the answersHashMap with a correctly answered question variable
        QuizAnswers quizAnswers = answersHashMap.get(currentAnnotation.getId());
        quizAnswers.setAnswerStatus(ANSWER_INCORRECT);
        // Creates a tempButton to determine which to enact changes on
        Button tempButton = new Button(getContext());
        // Switches based on the id of the button clicked to determine which answer was chosen
        switch(view.getId())
        {
            // Assigns the correct button on GUI to tempButton and sets the quizAnswer answerText
            case R.id.answer_button_1:
                tempButton = view.findViewById(R.id.answer_button_1);
                quizAnswers.setAnswerText(tempButton.getText().toString());
                break;
            case R.id.answer_button_2:
                tempButton = view.findViewById(R.id.answer_button_2);
                quizAnswers.setAnswerText(tempButton.getText().toString());
                break;
            case R.id.answer_button_3:
                tempButton = view.findViewById(R.id.answer_button_3);
                quizAnswers.setAnswerText(tempButton.getText().toString());
                break;
            case R.id.answer_button_4:
                tempButton = view.findViewById(R.id.answer_button_4);
                quizAnswers.setAnswerText(tempButton.getText().toString());
                break;

        }
        // General method to change visuals of incorrect button click
        doIncorrectAnswerButtonChanges(tempButton);
        // Increments number of quiz questions answered
        noQuizAnswersCompleted +=1;
    }

    /**
     * Creates a hint for the using the Bodypart or Category field from the annotation
     */
    public void hintCreation()
    {
        // Constructs an AlertDialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));
        // Randomly chooses which type of hint to give
        int randomHint = (int)(Math.random()*(1-0+1) + 0);
        if(randomHint == HINT_CATEGORY)
        {
            List<String> categories = currentAnnotation.getCategory();
            // Randomly chooses which category if multiple to show as the hint
            int randomCat = (int)(Math.random()*((categories.size()-1)-0+1) + 0);
            alertDialogBuilder.setMessage("A category is " + categories.get(randomCat));
            alertDialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        else if (randomHint == HINT_BODYPART)
        {
            List<String> bodyparts = currentAnnotation.getBodyPart();
            // Randomly chooses which bodypart if multiple to show as the hint
            int randomBodyPart = (int)(Math.random()*((bodyparts.size()-1)-0+1) + 0);
            alertDialogBuilder.setMessage("A bodypart is " + bodyparts.get(randomBodyPart));
            alertDialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }

        // Displays the alertDialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.PrimaryText, null));
    }

    /**
     * Changes the visible aspects and behaviour of buttons when correct answer is selected
     */
    private void doCorrectAnswerButtonChanges()
    {
        correctAnswer.setBackgroundColor(getResources().getColor(R.color.AnswerCorrect, null));
        correctAnswer.setHighlightColor(getResources().getColor(R.color.SelectedHighlight, null));
        makeAnswerButtonsUnclickable();
    }

    /**
     * Changes the visible aspects and behaviour of buttons when incorrect answer is selected
     * @param tempButton
     */
    private void doIncorrectAnswerButtonChanges(Button tempButton)
    {
        tempButton.setBackgroundColor(getResources().getColor(R.color.AnswerIncorrect, null));
        tempButton.setHighlightColor(getResources().getColor(R.color.SelectedHighlight, null));
        // also displays the correct answer by using this method
        doCorrectAnswerButtonChanges();
    }

    /**
     * Makes all button unclickable
     */
    private void makeAnswerButtonsUnclickable()
    {
        correctAnswer.setClickable(false);
        answer1.setClickable(false);
        answer2.setClickable(false);
        answer3.setClickable(false);
    }

    /**
     * Calls the playerActivity onQuizComplete method if noQuizAnswersCompleted is equal to the HashMapSize when
     * the fragment is destroyed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(answersHashMap.size() == noQuizAnswersCompleted) {
            playerActivity.onQuizComplete();
        }
    }
}