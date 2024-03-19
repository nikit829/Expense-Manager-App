
package com.example.budgetbuddy;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetbuddy.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

public class DashboardFragment extends Fragment {
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    private boolean isOpen=false;

    //object of animation class
    private Animation fadeOpen,fadeClose;
    private TextView totalIncomeResult,totalExpenseResult;
    //firebase object
    private FirebaseAuth Auth;
    private DatabaseReference incomeDatabase;
    private DatabaseReference expenseDatabase;

    //recycler view
    private RecyclerView recyclerIncome,recyclerExpense;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dashboard, container, false);

        //firebase connection
        Auth = FirebaseAuth.getInstance();
        FirebaseUser user = Auth.getCurrentUser();
        //uid for unique data for unique user
        String uid = user.getUid();

        incomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        expenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);


        //connect floating button to layout
        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_ft_btn);

        //connect floating text
        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);

        //Total income and expense result
        totalIncomeResult= myview.findViewById(R.id.income_set_result);
        totalExpenseResult= myview.findViewById(R.id.expense_set_result);

        //Recycler view data
        recyclerIncome = myview.findViewById(R.id.recycler_income_dashboard);
        recyclerExpense = myview.findViewById(R.id.recycler_expense_dashboard);

        //Animation connection
        fadeOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        fadeClose= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
                if(isOpen){
                    fab_income_btn.startAnimation(fadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.startAnimation(fadeClose);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(fadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.startAnimation(fadeClose);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;
                }
                else{
                    fab_income_btn.startAnimation(fadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.startAnimation(fadeOpen);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(fadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.startAnimation(fadeOpen);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;
                }
            }
        });

        //Calculate total income
        incomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int totalValue = 0;
                    for (DataSnapshot mySnapshot: snapshot.getChildren()){
                        Data data = mySnapshot.getValue(Data.class);
                        totalValue += data.getAmount();
                        String STtotalValue = String.valueOf(totalValue);
                        totalIncomeResult.setText(STtotalValue);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        expenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalValue = 0;
                for (DataSnapshot mySnapshot: snapshot.getChildren()){
                    Data data = mySnapshot.getValue(Data.class);
                    totalValue += data.getAmount();
                    String STtotalValue = String.valueOf(totalValue);
                    totalExpenseResult.setText(STtotalValue);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //recycler layout manager
        //for income
        LinearLayoutManager layoutManagerIncome= new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        recyclerIncome.setHasFixedSize(true);
        recyclerIncome.setLayoutManager(layoutManagerIncome);

        //for expense
        LinearLayoutManager layoutManagerExpense= new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        recyclerExpense.setHasFixedSize(true);
        recyclerExpense.setLayoutManager(layoutManagerExpense);

        return myview;
    }
    private void addData(){
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();
            }
        });
        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });
    }
    public void incomeDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = LayoutInflater.from(getActivity());
        View myview = inflator.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);
        AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        EditText editAmount=myview.findViewById(R.id.amount);
        EditText editType = myview.findViewById(R.id.type_edit);
        EditText editNote = myview.findViewById(R.id.note_edit);
        Button saveBtn = myview.findViewById(R.id.savebtn);
        Button cancelBtn = myview.findViewById(R.id.cancelbtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = editAmount.getText().toString();
                String type = editType.getText().toString();
                String note = editNote.getText().toString();

                if(TextUtils.isEmpty(amount)){
                    editAmount.setError("Field required");
                    return;
                }
                int intAmount=Integer.parseInt(amount);
                if(TextUtils.isEmpty(type)){
                    editType.setError("Field required");
                    return;
                }

                String id = incomeDatabase.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());
                Data data =  new Data(intAmount,type,note,id,date);
                incomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(),"Income data added",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void expenseDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = LayoutInflater.from(getActivity());
        View myview = inflator.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);
        AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        EditText amount = myview.findViewById(R.id.amount);
        EditText type = myview.findViewById(R.id.type_edit);
        EditText note = myview.findViewById(R.id.note_edit);

        Button saveBtn = myview.findViewById(R.id.savebtn);
        Button cancelBtn = myview.findViewById(R.id.cancelbtn);

         saveBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String Samount=amount.getText().toString();
                 String Stype=type.getText().toString();
                 String Snote=note.getText().toString();

                 if(TextUtils.isEmpty(Samount)){
                     amount.setError("Field Required");
                     return;
                 }
                 int intAmount=Integer.parseInt(Samount);
                 if(TextUtils.isEmpty(Stype)){
                     type.setError("Field Required");
                     return;
                 }
                 String id = expenseDatabase.push().getKey();
                 String date = DateFormat.getDateInstance().format(new Date());
                 Data data =  new Data(intAmount,Stype,Snote,id,date);
                 expenseDatabase.child(id).setValue(data);
                 Toast.makeText(getActivity(),"Expense data added",Toast.LENGTH_LONG).show();
                 dialog.dismiss();
             }
         });

         cancelBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 dialog.dismiss();
             }
         });
         dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(incomeDatabase, Data.class)
                        .build();
        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder viewHolder, int position, @NonNull Data model) {
                    viewHolder.setIncomeType(model.getType());
                    viewHolder.setIncomeAmount(model.getAmount());
                    viewHolder.setIncomeDate(model.getDate());
            }
            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income, parent, false);
                return new IncomeViewHolder(view);
            }
        };
        recyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();

        //firebase reading Expense data
        FirebaseRecyclerOptions<Data> options2 =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(expenseDatabase, Data.class)
                        .build();
        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(options2) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder viewHolder, int position, @NonNull Data model) {
                    viewHolder.setExpenseType(model.getType());
                    viewHolder.setExpenseAmount(model.getAmount());
                    viewHolder.setExpenseDate(model.getDate());

            }
            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense, parent, false);
                return new ExpenseViewHolder(view);
            }
        };
        recyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();
    }

    //for Income data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{
        View incomeView;
        public IncomeViewHolder(View itemView){
            super(itemView);
            incomeView=itemView;
        }
        public void setIncomeType(String type){
            TextView Dtype=incomeView.findViewById(R.id.type_income_dashboard);
            Dtype.setText(type);
        }
        public void setIncomeAmount(int amount){
            TextView Damount = incomeView.findViewById(R.id.amount_income_dashboard);
            String Stamount = String.valueOf(amount);
            Damount.setText(Stamount);
        }
        public void setIncomeDate(String date){
            TextView Ddate=incomeView.findViewById(R.id.date_income_dashboard);
            Ddate.setText(date);
        }
    }
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{
        View expenseView;
        public ExpenseViewHolder(View itemView){
            super(itemView);
            expenseView=itemView;
        }
        public void setExpenseType(String type){
            TextView Dtype=expenseView.findViewById(R.id.type_expense_dashboard);
            Dtype.setText(type);
        }
        public void setExpenseAmount(int amount){
            TextView Damount = expenseView.findViewById(R.id.amount_expense_dashboard);
            String Stamount = String.valueOf(amount);
            Damount.setText(Stamount);
        }
        public void setExpenseDate(String date){
            TextView Ddate=expenseView.findViewById(R.id.date_expense_dashboard);
            Ddate.setText(date);
        }
    }

}