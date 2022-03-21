package felix.andrea.misnotas

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nota_layout.*
import kotlinx.android.synthetic.main.nota_layout.view.*
import java.io.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    var notas = ArrayList<Nota>()
    lateinit var adaptador: AdaptadorNotas


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fab.setOnClickListener{
            var intent = Intent(this,AgregarNotaActivity::class.java)
            startActivityForResult(intent,123)
        }

        leerNotas()

        adaptador = AdaptadorNotas(this,notas)
        listview.adapter = adaptador

    }

    fun leerNotas(){
        notas.clear()
        var carpeta = File(ubicacion().absolutePath)

        if(carpeta.exists()){
            var archivos = carpeta.listFiles()
            if(archivos != null){
                for (archivo in archivos){
                    leerArchivo(archivo)
                }
            }
    }

    }

    private fun ubicacion(): File {
        val carpeta = File(this?.getExternalFilesDir(null),"notas")
        if (!carpeta.exists()){
            carpeta.mkdir()
        }

        return carpeta
    }

    private fun leerArchivo(archivo: File){
        val fis = FileInputStream(archivo)
        val di = DataInputStream(fis)
        val br = BufferedReader(InputStreamReader(di))
        var strLine: String? = br.readLine()
        var myData= ""

        while(strLine != null){
            myData= myData + strLine
            strLine = br.readLine()
        }

        br.close()
        di.close()
        fis.close()

        var nombre = archivo.name.substring(0, archivo.name.length-4)
        var nota = Nota(nombre,myData)
        notas.add(nota)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 123){
            leerNotas()
            adaptador.notifyDataSetChanged()
        }

    }

    class AdaptadorNotas: BaseAdapter {
        var context: Context
        var notas = ArrayList<Nota>()

        constructor(context: Context, notas: ArrayList<Nota>){
            this.context = context
            this.notas = notas
        }

        override fun getCount(): Int {
           return notas.size
        }

        override fun getItem(p0: Int): Any {
            return notas[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var inflador = LayoutInflater.from(context)
            var vista = inflador.inflate(R.layout.nota_layout,null)
            var nota = notas[p0]

            var titulo: TextView = vista.findViewById(R.id.tv_titulo_det)
            var contenido: TextView = vista.findViewById(R.id.tv_contenido_det)

            titulo.setText(nota.titulo)
            contenido.setText(nota.contenido)

            vista.btnBorrar.setOnClickListener{
                eliminar(nota.titulo)
                notas.remove(nota)
                this.notifyDataSetChanged()
            }

            return vista
        }

        private fun eliminar(titulo: String){
            if(titulo == ""){
                Toast.makeText(context,"Error: Titulo vacio",Toast.LENGTH_SHORT).show()
            } else {
                try{
                    val archivo = File(ubicacion(),titulo+".txt")
                    archivo.delete()
                    Toast.makeText(context,"Se elimino el archivo",Toast.LENGTH_SHORT).show()
                } catch(e: Exception){
                    Toast.makeText(context,"Error: No se pudo eliminar el archivo",Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun ubicacion(): String {
            val carpeta = File(context?.getExternalFilesDir(null),"notas")
            if (!carpeta.exists()){
                carpeta.mkdir()
            }

            return carpeta.absolutePath
        }

    }


}