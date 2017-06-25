package com.apjoex.pdfmaker;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Context context;
    Button proceed_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        proceed_btn = (Button)findViewById(R.id.proceed_btn);

        //
        proceed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            10);
                }else{
                    createPdf();
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void createPdf() {

        // create a new document
        PdfDocument document = new PdfDocument();

        // create a page description using A4 size dimensions
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder((int)Utils.convertPixelsToDp(2480,context), (int) Utils.convertPixelsToDp(3508,context), 1).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        Rect rect = new Rect();
        rect.set(0, 0, (int)Utils.convertPixelsToDp(2480,context), (int)Utils.convertPixelsToDp(70,context));

        //Make a new view and lay it out at the desired Rect dimensions
        TextView view = new TextView(this);
        view.setText("This is a custom drawn textview");
        view.setBackgroundColor(Color.RED);
        view.setGravity(Gravity.CENTER);

        //Measure the view at the exact dimensions (otherwise the text won't center correctly)
        int widthSpec = View.MeasureSpec.makeMeasureSpec(rect.width(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(rect.height(), View.MeasureSpec.EXACTLY);
        view.measure(widthSpec, heightSpec);

        //Lay the view out at the rect width and height
        view.layout(0, 0, rect.width(), rect.height());

        Canvas canvas = page.getCanvas();

        //Translate the Canvas into position and draw it
        canvas.save();
        canvas.translate(rect.left, rect.top);
        view.draw(canvas);
        canvas.translate(10,100);
        canvas.drawText("This is a second line", 10, 10, new Paint());
        canvas.translate(0, 40);
        canvas.drawText(getString(R.string.long_text),10,10, new Paint());
        canvas.translate(0,40);
        getWindow().getDecorView().draw(canvas);


        document.finishPage(page);
        try {
            File f = new File(Environment.getExternalStorageDirectory().getPath() + "/Invoice_demo.pdf");
            FileOutputStream fos = new FileOutputStream(f);
            document.writeTo(fos);
            document.close();
            fos.close();
            showPdf();
        } catch (IOException e) {
            throw new RuntimeException("Error generating file", e);
        }
    }

    private void showPdf() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ "Invoice_demo.pdf");
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file),"application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(MainActivity.this, "Please install a PDF reader", Toast.LENGTH_SHORT).show();
        }
    }
}
