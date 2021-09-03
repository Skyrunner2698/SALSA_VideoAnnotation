package com.example.salsa_videoannotation;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import static com.example.salsa_videoannotation.MainActivity.annotationWrapperList;
import static com.example.salsa_videoannotation.PlayerActivity.answersHashMap;
import static com.example.salsa_videoannotation.PlayerActivity.noQuizAnswersCompleted;
import static com.example.salsa_videoannotation.PlayerActivity.quizCompleted;
import static com.example.salsa_videoannotation.PlayerActivity.quizScore;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuizAnsweringFragment extends Fragment {
    private TextView question;
    private Button answer1;
    private Button answer2;
    private Button answer3;
    private Button correctAnswer;
    private PlayerActivity playerActivity;
    private Annotations currentAnnotationWrapper;
    private AnnotationData currentAnnotation;
    private View view;
    public static final int UNANSWERED = 0;
    public static final int ANSWER_CORRECT = 1;
    public static final int ANSWER_INCORRECT = 2;
    private static final int HINT_CATEGORY = 0;
    private static final int HINT_BODYPART = 1;


    public QuizAnsweringFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_quiz_answering, container, false);
        question = view.findViewById(R.id.question_display);
        playerActivity = (PlayerActivity) getActivity();
        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            String annotationWrapperId = bundle.getString("annotationWrapperId");
            int annotationId = bundle.getInt("annotationId");
            currentAnnotationWrapper = annotationWrapperList.get(annotationWrapperId);
            currentAnnotation = currentAnnotationWrapper.getVideoAnnotationsMap().get(annotationId);
            if(answersHashMap.get(currentAnnotation.getId()).getAnswerStatus() != UNANSWERED)
            {
                QuizAnswers quizAnswers = answersHashMap.get(currentAnnotation.getId());
                setUpAnsweredButtons(quizAnswers);
            }
            else
            {
                assignButtonsToAnswers();
                setButtonText();
                setOnclickListeners();
            }
            playerActivity.simpleExoPlayer.seekTo(currentAnnotation.getStartTime());
            playerActivity.simpleExoPlayer.setPlayWhenReady(false);
        }
        return view;
    }

    private void setUpAnsweredButtons(QuizAnswers quizAnswers)
    {
        correctAnswer = view.findViewById(R.id.answer_button_1);
        answer1 = view.findViewById(R.id.answer_button_2);
        answer2 = view.findViewById(R.id.answer_button_3);
        answer3 = view.findViewById(R.id.answer_button_4);
        setButtonText();
        if(quizAnswers.getAnswerStatus() == ANSWER_CORRECT)
        {
            doCorrectAnswerButtonChanges();
        }
        else
        {
            if(answer1.getText().toString().equals(quizAnswers.getAnswerText()))
                doIncorrectAnswerButtonChanges(answer1);
            else if(answer2.getText().toString().equals(quizAnswers.getAnswerText()))
                doIncorrectAnswerButtonChanges(answer2);
            else if(answer3.getText().toString().equals(quizAnswers.getAnswerText()))
                doIncorrectAnswerButtonChanges(answer3);
        }
        correctAnswer.setClickable(false);
        answer1.setClickable(false);
        answer2.setClickable(false);
        answer3.setClickable(false);
    }

    public void assignButtonsToAnswers()
    {
        int randomNum = (int)(Math.random()*(4-1+1) + 1);
        switch (randomNum)
        {
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


    private void setButtonText()
    {
        question.setText(currentAnnotation.getQuizQuestion().getQuestion());
        answer1.setText(currentAnnotation.getQuizQuestion().getAnswer1());
        answer2.setText(currentAnnotation.getQuizQuestion().getAnswer2());
        answer3.setText(currentAnnotation.getQuizQuestion().getAnswer3());
        correctAnswer.setText(currentAnnotation.getQuizQuestion().getCorrectAnswer());
    }

    private void setOnclickListeners()
    {
        correctAnswer.setOnClickListener(this::onCorrectAnswerClick);
        answer1.setOnClickListener(this::onIncorrectAnswerClick);
        answer2.setOnClickListener(this::onIncorrectAnswerClick);
        answer3.setOnClickListener(this::onIncorrectAnswerClick);
    }


    public void onCorrectAnswerClick(View view)
    {
        doCorrectAnswerButtonChanges();
        QuizAnswers quizAnswers = answersHashMap.get(currentAnnotation.getId());
        quizAnswers.setAnswerText(correctAnswer.getText().toString());
        quizAnswers.setAnswerStatus(ANSWER_CORRECT);
        answersHashMap.replace(currentAnnotation.getId(), quizAnswers);
        quizScore += 1;
        noQuizAnswersCompleted +=1;
    }

    public void onIncorrectAnswerClick(View view)
    {
        QuizAnswers quizAnswers = answersHashMap.get(currentAnnotation.getId());
        quizAnswers.setAnswerStatus(ANSWER_INCORRECT);
        Button tempButton = new Button(getContext());
        switch(view.getId())
        {
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
        }
        doIncorrectAnswerButtonChanges(tempButton);
        noQuizAnswersCompleted +=1;
    }

    public void hintCreation()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));
        int randomHint = (int)(Math.random()*(1-0+1) + 0);
        if(randomHint == HINT_CATEGORY)
        {
            List<String> categories = currentAnnotation.getCategory();
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
            int randomCat = (int)(Math.random()*((bodyparts.size()-1)-0+1) + 0);
            alertDialogBuilder.setMessage("A bodypart is " + bodyparts.get(randomCat));
            alertDialogBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.PrimaryText, null));
    }

    private void doCorrectAnswerButtonChanges()
    {
        correctAnswer.setBackgroundColor(getResources().getColor(R.color.AnswerCorrect, null));
        correctAnswer.setHighlightColor(getResources().getColor(R.color.SelectedHighlight, null));
        makeAnswerButtonsUnclickable();
    }

    private void doIncorrectAnswerButtonChanges(Button tempButton)
    {
        tempButton.setBackgroundColor(getResources().getColor(R.color.AnswerIncorrect, null));
        tempButton.setHighlightColor(getResources().getColor(R.color.SelectedHighlight, null));
        doCorrectAnswerButtonChanges();
    }

    private void makeAnswerButtonsUnclickable()
    {
        correctAnswer.setClickable(false);
        answer1.setClickable(false);
        answer2.setClickable(false);
        answer3.setClickable(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(answersHashMap.size() == noQuizAnswersCompleted) {
            playerActivity.onQuizComplete();
        }
    }
}