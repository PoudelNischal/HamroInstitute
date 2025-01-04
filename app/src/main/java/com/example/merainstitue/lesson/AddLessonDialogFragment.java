//package com.example.merainstitue.lesson;
//
//import android.app.Dialog;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.fragment.app.DialogFragment;
//
//import com.example.merainstitue.R;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class AddLessonDialogFragment extends DialogFragment {
//
//    private EditText titleEditText, descriptionEditText, videoUrlEditText;
//    private Button addLessonButton;
//    private String courseId;
//
//    public static AddLessonDialogFragment newInstance(String courseId) {
//        AddLessonDialogFragment fragment = new AddLessonDialogFragment();
//        Bundle args = new Bundle();
//        args.putString("COURSE_ID", courseId);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Retrieve courseId passed to the dialog
//        courseId = getArguments().getString("COURSE_ID");
//
//        // Inflate the layout for the dialog
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_lesson_dialog, null);
//
//        titleEditText = view.findViewById(R.id.lessonTitle);
//        descriptionEditText = view.findViewById(R.id.lessonDescription);
////        videoUrlEditText = view.findViewById(R.id.video_title);
//        addLessonButton = view.findViewById(R.id.addLessonButton);
//
//        // Set up the dialog's add lesson button
//        addLessonButton.setOnClickListener(v -> {
//            String title = titleEditText.getText().toString().trim();
//            String description = descriptionEditText.getText().toString().trim();
//            String videoUrl = videoUrlEditText.getText().toString().trim();
//
//            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(videoUrl)) {
//                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            } else {
//                // Create Lesson object and add it to Firestore
//                addLessonToFirestore(title, description, videoUrl);
//                dismiss();
//            }
//        });
//
//        // Set up the dialog
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setView(view);
//        builder.setTitle("Add New Lesson");
//
//        return builder.create();
//    }
//
//    private void addLessonToFirestore(String title, String description, String videoUrl) {
//        // Create a Lesson object and add it to Firestore
//        Lesson lesson = new Lesson(title, description, videoUrl, courseId);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("Lessons")
//                .add(lesson)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(getActivity(), "Lesson added successfully", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(getActivity(), "Error adding lesson", Toast.LENGTH_SHORT).show();
//                });
//    }
//}
