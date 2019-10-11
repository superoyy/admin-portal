package com.dukla.portal.admin.web.base;

import com.mongodb.gridfs.GridFSFile;
import com.timanetworks.iov.core.jpa.handler.HibernateHandler;
import com.timanetworks.iov.core.mongodb.handler.MongodbHandler;
import com.timanetworks.iov.core.sql.handler.SqlHandler;
import com.timanetworks.iov.util.Kit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class GenericAction {

    @Autowired
    protected HibernateHandler hibernateHandler;

    @Autowired
    protected SqlHandler sqlHandler;

    @Autowired
    protected MongodbHandler mongodbHandler;


    /**
     * 得到分页对象
     * @param page
     * @param size
     * @return
     */
    protected Page getPage(String page,String size){
        int start = 0;
        int pageSize = 20;
        if(page!=null && size!=null){
            pageSize = Integer.parseInt(size);
            start = (Integer.parseInt(page)-1) * pageSize;
        }
        return new Page(start,pageSize);
    }

    protected class Page {

        private int start;

        private int count;

        Page(int start,int count){
            this.start = start;
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public int getStart() {
            return start;
        }
    }

    /**
     * 保存文件到MongoDbGridFs
     */
    protected GridFSFile saveUploadFileToGridFs(MultipartFile file) throws IOException {
        String orgName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        Map<String,Object> meta=new HashMap<String,Object>();
        meta.put("orgName",orgName);
        meta.put("ext",ext);
        return this.mongodbHandler.saveFile(file.getInputStream(),Kit.getRandomFileName()+ext,getContentType(ext),meta);
    }

    private String getContentType(String ext){
        String contentType="application/octet-stream";
        if(ext!=null && ext.length()!=0){
            ext=ext.toLowerCase();
            if(ext.equals(".jpeg") || ext.equals(".jpg")){
                contentType="image/jpeg";
            }else if(ext.equals("png")){
                contentType="image/png";
            }else if(ext.equals(".gif")){
                contentType="image/gif";
            }else if(ext.equals(".bmp")){
                contentType="image/bmp";
            }else if(ext.equals(".txt")){
                contentType="text/plain";
            }else if(ext.equals(".htm") || ext.equals(".html")){
                contentType="text/html";
            }
        }
        return contentType;
    }


    /**
     * 保存上传文件到本地文件系统
     */
    protected SaveMeta saveUploadFileToLocalFs(MultipartFile file, String path) throws IOException {
        String orgName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        long size = file.getSize();
        if (!path.startsWith("/") && !path.startsWith(".")) {
            path = "/" + path;
        }
        File saveDir = new File(CoreConstant.SYS_PROP.get(CoreConstant.CONTEXT_REAL_PATH) + path);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        String saveName = Kit.getRandomFileName();
        String savePath = saveDir.getAbsolutePath() + "/" + saveName + ext;
        file.transferTo(new File(savePath));
        String url = path + "/" + saveName + ext;

        SaveMeta saveMeta = new SaveMeta();
        saveMeta.setOrgName(orgName);
        saveMeta.setExt(ext);
        saveMeta.setSize(size);
        saveMeta.setSaveName(saveName);
        saveMeta.setSavePath(savePath);
        saveMeta.setUrl(url);
        return saveMeta;
    }

    protected class SaveMeta {
        private String ext;
        private long size;
        private String orgName;
        private String saveName;
        private String savePath;
        private String url;

        public String getExt() {
            return ext;
        }

        public void setExt(String ext) {
            this.ext = ext;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getSaveName() {
            return saveName;
        }

        public void setSaveName(String saveName) {
            this.saveName = saveName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getSavePath() {
            return savePath;
        }

        public void setSavePath(String savePath) {
            this.savePath = savePath;
        }
    }

}
