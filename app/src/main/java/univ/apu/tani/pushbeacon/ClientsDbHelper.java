package univ.apu.tani.pushbeacon;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tani on 2018/05/26.
 */

public class ClientsDbHelper extends SQLiteOpenHelper {

    /** データベースのバージョン */
    public static final int DB_VERSION = 1;  // データベーススキーマを変更した場合、インクリメントする。

    /** データベースファイル名 */
    public static final String DB_NAME = "clients.db";

    public ClientsDbHelper(Context context) {
        // SQLiteOpenHelperクラスのコンストラクタには
        // データベースのバージョンを渡す。
        // コンストラクタに渡したバージョンと
        // データベースが保持しているバージョンが異なる場合、
        // onUpgrade()、またはonDowngrade()が呼ばれる。

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // データベースが作成された時に呼び出される。
        // ここでデータベースの初期化(テーブルの作成やテーブルの初期化等)を行う。

        final String createSql = "CREATE TABLE clients("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT"
                + ")";
        try {
            db.execSQL(createSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // データベースをアップグレードする必要がある場合に呼び出される。
        // ここでデータベースを新バージョンにアップグレードする処理を行う。
        // 以下は、既存データはすべて破棄しても良い場合の処理例

        db.execSQL("DROP TABLE clients"); // 旧バージョンのテーブルを破棄
        onCreate(db); // 新バージョンでテーブルを作成し直す
    }

}
