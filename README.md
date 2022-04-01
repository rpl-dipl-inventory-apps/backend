# inventroy-app-BE
Inventory application BE build with java.

S1 INFORMATICS - TELKOM UNIVERSITY 

Sebuah aplikasi manajemen barang yang berfungsi untuk mempermudah hidup seseorang agar tidak perlu melakukan pencatatan barang secara manual.

# Build With 

- React JS
- Java
- Spring Boot

# Docs API
[Klik disini untuk dokumentasi API](https://documenter.getpostman.com/view/10131591/UVRBo6rL)

## Fix `cannot run program 'cmd'` error pada netbeans
1. Buka file `netbeans.conf` di directory `C:\Program Files\NetBeans 8.2\etc`
2. Cari keyword `netbeans_default_options` yang muncul pertama
3. Copy string `-J-Djdk.lang.Process.allowAmbiguousCommands=true` ke dalam `netbeans_default_options` ![image](https://user-images.githubusercontent.com/58929520/147031817-9edbce05-4ea6-41cf-bd7a-a7ca83090bcc.png)
4. Save file, restart netbeans
