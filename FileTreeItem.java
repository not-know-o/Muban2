package main;

import static main.Main.ROOT_FILE;
import java.io.File;
import java.util.function.Function;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 *
 * @author MK
 */
public class FileTreeItem extends TreeItem<String> {

    //判断树节点是否被初始化，没有初始化为真
    private boolean notInitialized = true;

    private final File file;
    private final Function<File,File[]> supplier;

    public FileTreeItem(File file) {
        super(Icon.getFileName(file), Icon.getFileIconToNode(file));
        this.file = file;
        supplier = (File f) -> {
            if (((FileTreeItem) this.getParent()).getFile() == ROOT_FILE) {
                String name = Icon.getFileName(f);
                if (name.equals("网络") || name.equals("家庭组")) {
                    return new File[0];
                }
            }
            return f.listFiles();
        };
    }
    public FileTreeItem(File file, Function<File,File[]> supplier) {
        super(Icon.getFileName(file), Icon.getFileIconToNode(file));
        this.file=file;
        this.supplier=supplier;
    }


    //重写getchildren方法，让节点被展开时加载子目录
    @Override
    public ObservableList<TreeItem<String>> getChildren() {

        ObservableList<TreeItem<String>> children = super.getChildren();
        //没有加载子目录时，则加载子目录作为树节点的孩子
        if (this.notInitialized && this.isExpanded()) {

            this.notInitialized = false;    //设置没有初始化为假

            /*判断树节点的文件是否是目录，
             *如果是目录，着把目录里面的所有的文件目录添加入树节点的孩子中。
             */

            if (this.getFile().isDirectory()) {
                for (File f : supplier.apply(this.getFile())) {
                    //如果文件是目录，则把它加到树节点上
                    if (f.isDirectory()) {
                        children.add(new FileTreeItem(f));
                    }
                }

            }
        }
        return children;
    }

    //重写叶子方法，如果该文件不是目录，则返回真
    @Override
    public boolean isLeaf() {

        return !file.isDirectory();
    }

    public File getFile() {
        return file;
    }


}