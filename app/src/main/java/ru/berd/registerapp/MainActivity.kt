package ru.berd.registerapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var btnAdd: Button? = null //инициализируем переменные для связи с элементами экрана
    var btnRead: Button? = null
    var btnClear: Button? = null
    var etName: EditText? = null
    var etEmail: EditText? = null
    var dbData: TextView? = null
    var dbHelper: DBHelper? = null // объявляем переменную
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnAdd = findViewById<View>(R.id.btnAdd) as Button //находим кнопки
        btnAdd!!.setOnClickListener(this) //прописываем обработчики
        btnRead = findViewById<View>(R.id.btnRead) as Button
        btnRead!!.setOnClickListener(this)
        btnClear = findViewById<View>(R.id.btnClear) as Button
        btnClear!!.setOnClickListener(this)
        etName = findViewById<View>(R.id.etName) as EditText //находим поля ввода
        etEmail = findViewById<View>(R.id.etEmail) as EditText
        dbData = findViewById(R.id.dbData)
        dbHelper = DBHelper(this) //создаем экземпляр класса
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(v: View) { // в этом методе при нажатии на любую кнопку считываем значения текстовых полей и сохраняем в перем типа стринг
        val name = etName!!.text.toString()
        val email = etEmail!!.text.toString()
        val database: SQLiteDatabase = dbHelper!!.getWritableDatabase() // класс, предназначенный для управления базой данных SQLite
                                                                        // getWritableDatabase() нужен чтобы открыть и вернуть экземпляр БД, с которой будем работать
                                                                        // в классе SQLiteDatabase реализованы методы:
                                                                        // query() - для чтения данных из БД
                                                                        // insert() - для добавления данных в БД
                                                                        // delete() - для удаления данных из БД
                                                                        // update() - для изменения данных в БД

        val contentValues = ContentValues() // класс, используемый для добавления новых строк в таблицу (каждый объект класса представляет собой одну строку таблицы)
                                            // и выглядит как массив с именами столбцов и значениями которые ему соответствуют
        when (v.id) { //конструкция-аналог типа switch для разделения по отдельным кнопкам
            R.id.btnAdd -> {
                contentValues.put(DBHelper.KEY_NAME, name) // заполняем по типу имя поля(ключ) - значение, ID заполнится автоматически
                contentValues.put(DBHelper.KEY_MAIL, email)
                if(name != "" && email != ""){
                    database.insert(DBHelper.TABLE_CONTACTS, null, contentValues)//вставляем подготовленные строки в т-цу
                                                                                                //метод принимает имя таблицы и объект contentValues со вставл-ми
                                                                                                //зн-ми. Второй аргумент нужен для вставки в т-цу пустой строки
                                                                                                //сейчас это нам не нужно, поэтому передаём null
                } else {
                    dbData?.setText("Name or email entered incorrectly!")
                }
            }
            R.id.btnRead -> { // чтение записей из таблицы
                val cursor =
                    database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null)
                                  // на вход подается имя таблицы, список запрашиваемых полей, условие выборки, группировка и сортировка
                                  // т.к. нам это все не нужно, оставляем нулл
                                  // метод query вовзращает объект класса cursor - его можно рассматривать как набор строк с данными
                if (cursor.moveToFirst()) { //метод, перемещающий курсор на первую строку в результате запроса (проверяем, выбиралось ли что-нибудь в методе query)
                    val idIndex = cursor.getColumnIndex(DBHelper.KEY_ID) // getColumnIndex - возвращает индекс для столбца с указанным именем
                    val nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME) // и исключение если столбца не существует
                    val emailIndex = cursor.getColumnIndex(DBHelper.KEY_MAIL)
                    var temp: String? = ""
                    do {
                        temp += "ID = ${cursor.getInt(idIndex)},\nname = ${cursor.getString(nameIndex)},\nemail = ${cursor.getString(emailIndex)};\n\n"
                        dbData?.setText(temp)
                    } while (cursor.moveToNext()) // перебираем все строки в курсоре пока не добираемся до конца
                } else dbData?.setText("The database is empty!") // если записей нет, выводим сообщение
                cursor.close() // в конце закрываем курсор и освобождаем занимаемые им ресурсы методом close, т к далее мы его нигде не используем
            }
            R.id.btnClear -> database.delete(DBHelper.TABLE_CONTACTS, null, null) // передаем имя т-цы и условия (т к удаляем все записи, передаем null)
        }
        dbHelper!!.close() // закрываем соединение с БД методом close
    }
}