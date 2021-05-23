package ru.berd.registerapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) :
    SQLiteOpenHelper(// есть как обязательные(ниже) методы, так и не обязательные:
                     // onDowngrade - вызывается в ситуации, обратной onUpgrade
                     // onOpen - при открытии бд
                     // getReadableDatabase - возвращает БД для чтения
                     // getWritableDatabase возвращает БД для чтения и записи
        context, //контекст
        DATABASE_NAME, // имя БД
        null, //курсорфактори, расширяющий стандартный курсор (мы его здесь использовать не будем, поэтому занулим его)
        DATABASE_VERSION // версию БД
    ) {

    override fun onCreate(db: SQLiteDatabase) { //вызывается при первом создании базы данных (если ее не существует)
        db.execSQL( //метод, который выполняет определенный SQL-запрос
            "create table " + TABLE_CONTACTS + "(" + KEY_ID //создаем таблицу "Contacts" с полями ID, NAME, MAIL
                    + " integer primary key," + KEY_NAME + " text," + KEY_MAIL + " text" + ")"
        )
    }

    override fun onUpgrade( //вызывается при изменении базы данных, а именно если указанная в приложении версия бд выше чем в самой бд
                            //такая ситуация может иметь место при обновлении приложения, когда нужно заменить старую базу
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("drop table if exists $TABLE_CONTACTS") //происходит запрос в БД на ее уничтожение
        onCreate(db) // вновь вызываем онКреэйт для создания новой таблицы с обновленной структурой
    }

    companion object {//аналог использования ключевого слова static
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "contactDb"
        const val TABLE_CONTACTS = "contacts"
        const val KEY_ID = "_id"// константа для заголовков столбцов таблицы (_ - особенность платформы)
        const val KEY_NAME = "name"
        const val KEY_MAIL = "mail"
    }
}