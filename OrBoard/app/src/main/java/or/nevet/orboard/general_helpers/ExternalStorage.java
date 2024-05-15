package or.nevet.orboard.general_helpers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;


public class ExternalStorage {



    public static class ScopedStorageOperations {
        public static Uri getFileIfExists(Context context, String publicDirectoryName, String subDirectoryName, String fileName) {
            String selection = MediaStore.Files.FileColumns.DATA + " like ?";
            String[] selectionArgs = new String[]{"%"+ Environment.getExternalStoragePublicDirectory(publicDirectoryName)+"/"+subDirectoryName+"/"+fileName+"%"};
            String[] projection = {MediaStore.Files.FileColumns._ID};
            Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), projection, selection, selectionArgs, null);
            if (cursor == null)
                return null;
            if (!cursor.moveToFirst()) {
                cursor.close();
                return null;
            }
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
            Uri uri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id);
            cursor.close();
            return uri;
        }

        private static Uri createTextFile(Context context, String publicDirectoryName, String subDirectoryName, String fileName) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain");
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, publicDirectoryName + "/"+subDirectoryName);
            Uri uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
            writeTextToFile(uri, "", context);
            return uri;
        }

        public static void appendTextToFileOrCreateIfNotExisting(String fileName, String publicDirectoryName, String subDirectoryName, String newContent, Context context) {
            Uri fileUri = getFileIfExists(context, publicDirectoryName, subDirectoryName, fileName);
            if (fileUri == null)
                fileUri = createTextFile(context, publicDirectoryName, subDirectoryName, fileName);
            String fileText = getFileText(fileUri, context);
            writeTextToFile(fileUri, fileText.concat(newContent), context);
        }

        public static void deleteFile(Uri uri, Context context) {
            context.getContentResolver().delete(uri, null, null);
        }

        private static void writeTextToFile(Uri uri, String text, Context context) {
            try {
                OutputStream writer = context.getContentResolver().openOutputStream(uri);
                if (writer != null) {
                    writer.write(text.getBytes());
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static String getFileText(Uri uri, Context context) {
            try {
                InputStream is = context.getContentResolver().openInputStream(uri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }
                is.close();
                return builder.toString();
            } catch (IOException e) {
                return null;
            }
        }
    }

    public static class AppDirectoryOperations {

        public static String readFileFromAppDirectory(Context context, String name) throws FileNotFoundException {
            try {
                FileInputStream fileInputStream = context.openFileInput(name);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuffer stringBuffer = new StringBuffer();
                String lines;
                while ((lines = bufferedReader.readLine()) != null) {
                    stringBuffer.append(lines).append("\n");
                }
                if (stringBuffer.toString().isEmpty())
                    throw new FileNotFoundException();
                return stringBuffer.toString();
            } catch (IOException e) {
                if (!(e.getClass() == FileNotFoundException.class)) {
                    e.printStackTrace();
                    throw new RuntimeException("IOException in reading file " + name);
                } else
                    throw new FileNotFoundException();
            }
        }

        public static void writeFileToAppDirectory(Context context, String name, String content) {
            try {
                FileOutputStream fileOutputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
                fileOutputStream.write(content.getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void deleteFileFromAppDirectory(Context context, String name) {
            context.deleteFile(name);
        }

        public static void appendTextToFileInAppDirectory(Context context, String name, String content) {
            try {
                FileOutputStream fileOutputStream = context.openFileOutput(name, Context.MODE_APPEND);
                fileOutputStream.write(content.getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void saveObjectToAppDirectory(Context context, String fileName, Object o) {
            try {
                FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(o);
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void saveBitmapToAppDirectory(Bitmap bitmap, String fileName, Context context) {
            try {
                FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public static Bitmap restoreBitmapFromAppDirectory(String fileName, Context context) throws FileNotFoundException {
            return BitmapFactory.decodeStream(context.openFileInput(fileName));
        }

        public static <T> T restoreObjectFromAppDirectory(Context context, String fileName) throws IOException {
            try {
                FileInputStream fileInputStream = context.openFileInput(fileName);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                T o = (T) objectInputStream.readObject();
                objectInputStream.close();
                return o;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
