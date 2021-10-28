package com.caxetasystem.acessoweb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    //cria as permissões de acesso para gerar png e pdf
    private String[] permissoes = new String[]{

            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //validar permissões
        Permissoes.validarPermissoes(permissoes,this,1);

        webView = findViewById(R.id.webView);

        //página rodar dentro do app
        webView.setWebViewClient(new WebViewClient());

        // habilitar javascript
        webView.getSettings().setJavaScriptEnabled(true);

        //Armazenar dados no celular para agilizar a navegação
        webView.getSettings().setDomStorageEnabled(true);

        webView.loadUrl("http://matozinhos.mg.gov.br/");

        //Efetuar downloads pela página web
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimeType, long contentLength) {

                try {

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setMimeType(mimeType);
                    String cookies = CookieManager.getInstance().getCookie(url);
                    request.addRequestHeader("cookies", cookies);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Download iniciado...");
                    request.setTitle(URLUtil.guessFileName(url,contentDisposition,mimeType));
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,URLUtil.guessFileName(
                            url,contentDisposition,mimeType));
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Download iniciado...", Toast.LENGTH_SHORT).show();

                }catch (Exception ignored){

                    Toast.makeText(getApplicationContext(), "error " + ignored,
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    //Verificação de permissões
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults){
            if( permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }

    }
    private void alertaValidacaoPermissao(){

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

    }

    //evento para retornar a página anterior caso seja possível.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.itemSobre:
               informe();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void informe(){
        AlertDialog.Builder alertInforme = new AlertDialog.Builder(this);
        alertInforme.setTitle("INFORMAÇÕES: ");
        alertInforme.setMessage("Programador: Anderson P. Martins "+
                "Matozinhos Versão 1.0");
        alertInforme.setIcon(R.mipmap.ic_launcher);
        alertInforme.setCancelable(false);

        alertInforme.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alert = alertInforme.create();
        alert.show();
    }
}