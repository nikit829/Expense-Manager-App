package com.example.budgetbuddy;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.budgetbuddy.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class ExpenseFragment extends Fragment {

    private FirebaseAuth Auth;
    private DatabaseReference expenseDatabase;
    private RecyclerView recyclerView;
    private TextView expenseTotalAmount;

    //Edit data item
    private EditText editAmount,editType,editNote;
    private Button btnUpdate,btnDelete;
    private String type, note, post_key;
    private int amount;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);

        Auth = FirebaseAuth.getInstance();
        FirebaseUser user = Auth.getCurrentUser();
        String uid = user.getUid();
        expenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        expenseTotalAmount = myview.findViewById(R.id.expense_txt_result);

        recyclerView = myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        expenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalValue = 0;
                for (DataSnapshot mySnapshot: snapshot.getChildren()){
                    Data data = mySnapshot.getValue(Data.class);
                    totalValue += data.getAmount();
                    String STtotalValue = String.valueOf(totalValue);
                    expenseTotalAmount.setText(STtotalValue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = expenseDatabase.orderByChild("date"); // Adjust this based on your data structure

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(query, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseFragment.MyViewHolder viewHolder, int position, @NonNull Data model) {
                viewHolder.setType(model.getType());
                viewHolder.setDescription(model.getDiscription());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmount(model.getAmount());

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = getRef(viewHolder.getAdapterPosition()).getKey();
                        type = model.getType();
                        note = model.getDiscription();
                        amount = model.getAmount();
                        updateDataItem();
                    }
                });
            }

            @NonNull
            @Override
            public ExpenseFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false);
                return new ExpenseFragment.MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        void setType(String type) {
            TextView Itype = view.findViewById(R.id.type_txt_expense);
            Itype.setText(type);
        }

        void setDescription(String description) {
            TextView Inote = view.findViewById(R.id.note_txt_expense);
            Inote.setText(description);
        }

        void setDate(String date) {
            TextView Idate = view.findViewById(R.id.date_txt_expense);
            Idate.setText(date);
        }

        void setAmount(int amount) {
            TextView Iamount = view.findViewById(R.id.amount_txt_expense);
            String STamount = String.valueOf(amount);
            Iamount.setText(STamount);
        }
    }

    private void updateDataItem(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View view= inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(view);

        editAmount=view.findViewById(R.id.amount);
        editNote=view.findViewById(R.id.note_edit);
        editType=view.findViewById(R.id.type_edit);

        //set data to edit text
        editType.setText(type);
        editType.setSelection(type.length());
        editNote.setText(note);
        editNote.setSelection(note.length());
        editAmount.setText(String.valueOf(amount));  // Corrected line
        editAmount.setSelection(String.valueOf(amount).length());


        btnUpdate=view.findViewById(R.id.updatebtn);
        btnDelete=view.findViewById(R.id.deletebtn);

        AlertDialog dialog=mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = editType.getText().toString();
                note = editNote.getText().toString();
                String STamount = editAmount.getText().toString();
                amount = Integer.parseInt(STamount);  // Update the amount variable

                String date = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(amount, type, note, post_key, date); // Update the amount in Data

                expenseDatabase.child(post_key).setValue(data);
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
