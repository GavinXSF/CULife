package xsf_cym.culife;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class shortcutFragment extends Fragment {


    public static shortcutFragment newInstance() { return new shortcutFragment(); }
    public shortcutFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shortcut, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Button btn1 = getActivity().findViewById(R.id.sc_btn1);
        Button btn2 = getActivity().findViewById(R.id.sc_btn2);
        Button btn3 = getActivity().findViewById(R.id.sc_btn3);
        Button btn4 = getActivity().findViewById(R.id.sc_btn4);
        Button btn5 = getActivity().findViewById(R.id.sc_btn5);
        Button btn6 = getActivity().findViewById(R.id.sc_btn6);
        Button btn7 = getActivity().findViewById(R.id.sc_btn7);
        Button btn8 = getActivity().findViewById(R.id.sc_btn8);
        Button btn9 = getActivity().findViewById(R.id.sc_btn9);
        Button btn10 = getActivity().findViewById(R.id.sc_btn10);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.cuhk.edu.hk/chinese/campus/library-museum.html"));
                startActivity(intent);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.cuhk.edu.hk/chinese/campus/accommodation.html#canteen_info"));
                startActivity(intent);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://onepass.cuhk.edu.hk/login/index.jsp?resource_url=https%3A%2F%2Fportal.cuhk.edu.hk%2Fpsp%2Fepprd%2F%3FlanguageCd%3DENG"));
                startActivity(intent);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://sts.cuhk.edu.hk/adfs/ls/?SAMLRequest=fZFLb4MwEIT%2FCvI9GCiB1ApINDk0UtqiQHvopTLGFCvGpl7Tx78vCX0kl5x3dmb22yXQTvYkG2yrdvxt4GCdz04qIMdBggajiKYggCjacSCWkSK725LA9UhvtNVMS%2BRkANxYodVKKxg6bgpu3gXjj7ttglpreyAYmwpcKSqXDe3e5fXgtntctKKqtOS2dQE0PlgHOH8oSuSsxy5C0YPrvwdYONundQNYAkbOZp2glyoKq%2Fmc%2Bf4iYpEX1k0TxUEcLOI6iJo5i0cZwMA3CixVNkGB51%2FPvHAWXJVeRMIFCcJn5OQ%2Fd90IVQv1ehlCNYmA3JZlPpuqP3EDx9qjAKXLA0pyDDYncC%2Fb0l%2BiKL3ED%2F74LfFJzJTZk%2FvRd7POtRTsy8mk1B8rw6nlCfIRTqeV8%2B%2Bn3w%3D%3D&RelayState=ss%3Amem%3Aeda995a9751f907be0ded915641284d3e164fb5702d5dc217d5f05efd1b08ebe"));
                startActivity(intent);
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://academic.veriguide.org/academic/login_CUHK.jspx"));
                startActivity(intent);
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://sts.cuhk.edu.hk/adfs/ls/?SAMLRequest=fZHBUsIwFEV%2FpZM9TRsK2AztTIWFzKB2aHXhxknTYDOkSc1LRf9eoDrCQta577yb8%2BbAWtXRrHeN3oj3XoDzPlulgZ4eEtRbTQ0DCVSzVgB1nBbZ%2FZoSP6CdNc5wo5CXAQjrpNELo6FvhS2E%2FZBcPG3WCWqc64BivBcV6zpfOuA%2B75udL%2Breb3a4aGRVGSVc4wMYfMQTnD8WJfKWhz5SsyP5jwMOLuZZvQWsACNvtUzQ6ySOI0KqbV2HMxJGZBaMo5jX22AaTW%2FimB9iAL1YaXBMuwSRIIxHQTQi4zKY0iimk%2FAFefnP326lrqV%2Buy6iGkJA78oyHw3Vn4WFU%2B1DAKXzo056WmzPBF%2FHsl%2BrKP3H3RyfcYclHX04gFbL3CjJv7xMKbNfWMGcSFCIcDqMXJ48%2FQY%3D&RelayState=ss%3Amem%3Ac4b89fa3d37b4cfdaf228dde298860b1b8aafa896c0e74e9e8cdd39c47766cc7"));
                startActivity(intent);
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.cuhk.edu.hk/english/campus/accommodation.html"));
                startActivity(intent);
            }
        });
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.res.cuhk.edu.hk/zh-tw/examinations"));
                startActivity(intent);
            }
        });
        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.itsc.cuhk.edu.hk/"));
                startActivity(intent);
            }
        });
        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://cpdc.osa.cuhk.edu.hk/"));
                startActivity(intent);
            }
        });


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
