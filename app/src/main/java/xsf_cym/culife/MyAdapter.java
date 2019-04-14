package xsf_cym.culife;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MyAdapter extends BaseAdapter implements View.OnClickListener {
    //上下文
    private Context context;
    private ArrayList<String> data;
    private ArrayList<String> busNums;
    private ArrayList<Integer> stopIndex;
    private ArrayList<Boolean> isDue;
    public MyAdapter(ArrayList<String> data, ArrayList<String> busNum,ArrayList<Integer> stopIndex,ArrayList<Boolean> isDue){
        this.data = data;
        busNums = busNum;
        this.stopIndex = stopIndex;
        this.isDue = isDue;
    }
    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(context == null)
            context = viewGroup.getContext();
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.mTv = (TextView)view.findViewById(R.id.mTv);
            viewHolder.mBtn = (Button)view.findViewById(R.id.mBtn);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)view.getTag();
        viewHolder.mTv.setText(data.get(i));
        viewHolder.mTv.setOnClickListener(this);
        viewHolder.mBtn.setTag(R.id.btn,i);
        viewHolder.mBtn.setEnabled(isDue.get(i));
        viewHolder.mBtn.setText("Arrive");
        viewHolder.mBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mBtn:
                int index = (int) view.getTag(R.id.btn);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Connection connection = null;
                        try {
                            Class.forName("com.mysql.jdbc.Driver");
                            String jdbcUrl = String.format("jdbc:mysql://34.92.5.65:3306/culife");

                            connection = DriverManager.getConnection(jdbcUrl, "root", "carlos0923=-=");
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        } catch (ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                        try {
                            Statement st = connection.createStatement();
                            String sql = "INSERT INTO busInfo(busNum,stopIndex,time) VALUES('"+busNums.get(index)+ "','"+stopIndex.get(index)+"','"+System.currentTimeMillis()+"')";
                            int result = st.executeUpdate(sql);
                            connection.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }}).start();
                Log.d("Tsai", "Btn_onClick: " + "view = " + view);

                break;
            case R.id.mTv:
                Log.d("Tsai", "Tv_onClick: " + "view = " + view);

                break;
        }
    }

    static class ViewHolder{
        TextView mTv;
        Button mBtn;
    }

}
