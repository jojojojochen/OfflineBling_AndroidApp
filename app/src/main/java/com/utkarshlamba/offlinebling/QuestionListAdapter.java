package com.utkarshlamba.offlinebling;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Ryan on 2016-01-23.
 */
public class QuestionListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private ArrayList<String> questions;
    private ArrayList<String> answers;

    public QuestionListAdapter(Context context, ArrayList<String> qs, ArrayList<String> ans) {
        super(context, R.layout.faq_list_item, qs);
        this.context = context;
        questions = qs;
        answers = ans;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.faq_list_item, parent, false);
        TextView questionTextView = (TextView) rowView.findViewById(R.id.faq_question_label);
        TextView answerTextView = (TextView) rowView.findViewById(R.id.faq_answer_label);
        questionTextView.setText(questions.get(position));
        answerTextView.setText(answers.get(position));

        Button editButton = (Button) rowView.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int index;
                View parentRow = (View) v.getParent().getParent();
                ListView listView = (ListView) parentRow.getParent();
                index = listView.getPositionForView(parentRow);

                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.layout_answer_dialog);
                TextView questionTextView = (TextView)
                        dialog.findViewById(R.id.question_dialog_label);
                final EditText answerEditText = (EditText) dialog.findViewById(R.id.answer_editText);
                questionTextView.setText(MainActivity.questionsList.get(index));
                String answer = MainActivity.answersList.get(index);
                if (answer.equals("Not answered yet")){
                    answerEditText.setHint("Not answered yet");
                } else {
                    answerEditText.setText(MainActivity.answersList.get(index));
                }


                Button editButton = (Button) dialog.findViewById(R.id.submit_button);
                Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ans = answerEditText.getText().toString();
                        MainActivity.answersList.set(index, ans);
                        FAQFragment.adapter.notifyDataSetChanged();
                        ans = URLEncoder.encode(ans);
                        String q = URLEncoder.encode(MainActivity.questionsList.get(index));
                        new SendAnsToDBTask(q,ans).execute();
                        dialog.dismiss();

                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });


        return rowView;
    }
}
