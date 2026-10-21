package cn.ieasy.framework.base.filestorage.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.ieasy.framework.base.filestorage.convertor.StorageRecordConvertor;
import cn.ieasy.framework.base.filestorage.dto.StorageRecordDTO;
import cn.ieasy.framework.base.filestorage.enums.NormalizationType;
import cn.ieasy.framework.base.filestorage.model.StorageRecord;
import cn.ieasy.framework.base.filestorage.service.StorageService;
import cn.ieasy.framework.common.web.dto.result.ListResultDTO;
import cn.ieasy.framework.common.web.exception.CustomRuntimeException;

/**
 * 本地存储的上传、下载接口
 */
@RestController
public class LocalStoreController {

    private static final int DEFAULT_BUFFER_SIZE = 20480; // ..bytes = 20KB.
    private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";
    private static final long DEFAULT_EXPIRE_TIME = 604800000L; // ..ms = 1 // week.
    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final Map<String, String> contentTypes = new HashMap<String, String>() {
        private static final long serialVersionUID = 7227528070332985770L;

        {
            this.put("ez", "application/andrew-inset");
            this.put("hqx", "application/mac-binhex40");
            this.put("cpt", "application/mac-compactpro");
            this.put("doc", "application/msword");
            this.put("bin", "application/octet-stream");
            this.put("dms", "application/octet-stream");
            this.put("lha", "application/octet-stream");
            this.put("lzh", "application/octet-stream");
            this.put("exe", "application/octet-stream");
            this.put("class", "application/octet-stream");
            this.put("so", "application/octet-stream");
            this.put("dll", "application/octet-stream");
            this.put("oda", "application/oda");
            this.put("pdf", "application/pdf");
            this.put("ai", "application/postscript");
            this.put("json", "application/json");
            this.put("eps", "application/postscript");
            this.put("ps", "application/postscript");
            this.put("smi", "application/smil");
            this.put("smil", "application/smil");
            this.put("mif", "application/vnd.mif");
            this.put("xls", "application/vnd.ms-excel");
            this.put("ppt", "application/vnd.ms-powerpoint");
            this.put("wbxml", "application/vnd.wap.wbxml");
            this.put("wmlc", "application/vnd.wap.wmlc");
            this.put("wmlsc", "application/vnd.wap.wmlscriptc");
            this.put("bcpio", "application/x-bcpio");
            this.put("vcd", "application/x-cdlink");
            this.put("pgn", "application/x-chess-pgn");
            this.put("cpio", "application/x-cpio");
            this.put("csh", "application/x-csh");
            this.put("dcr", "application/x-director");
            this.put("dir", "application/x-director");
            this.put("dxr", "application/x-director");
            this.put("dvi", "application/x-dvi");
            this.put("spl", "application/x-futuresplash");
            this.put("gtar", "application/x-gtar");
            this.put("hdf", "application/x-hdf");
            this.put("js", "application/x-javascript");
            this.put("skp", "application/x-koan");
            this.put("skd", "application/x-koan");
            this.put("skt", "application/x-koan");
            this.put("skm", "application/x-koan");
            this.put("latex", "application/x-latex");
            this.put("nc", "application/x-netcdf");
            this.put("cdf", "application/x-netcdf");
            this.put("sh", "application/x-sh");
            this.put("shar", "application/x-shar");
            this.put("swf", "application/x-shockwave-flash");
            this.put("sit", "application/x-stuffit");
            this.put("sv4cpio", "application/x-sv4cpio");
            this.put("sv4crc", "application/x-sv4crc");
            this.put("tar", "application/x-tar");
            this.put("tcl", "application/x-tcl");
            this.put("tex", "application/x-tex");
            this.put("texinfo", "application/x-texinfo");
            this.put("texi", "application/x-texinfo");
            this.put("t", "application/x-troff");
            this.put("tr", "application/x-troff");
            this.put("roff", "application/x-troff");
            this.put("man", "application/x-troff-man");
            this.put("me", "application/x-troff-me");
            this.put("ms", "application/x-troff-ms");
            this.put("ustar", "application/x-ustar");
            this.put("src", "application/x-wais-source");
            this.put("xhtml", "application/xhtml+xml");
            this.put("xht", "application/xhtml+xml");
            this.put("zip", "application/zip");
            this.put("au", "audio/basic");
            this.put("snd", "audio/basic");
            this.put("mid", "audio/midi");
            this.put("midi", "audio/midi");
            this.put("kar", "audio/midi");
            this.put("mpga", "audio/mpeg");
            this.put("mp2", "audio/mpeg");
            this.put("mp3", "audio/mpeg");
            this.put("mp4", "video/mp4");
            this.put("aif", "audio/x-aiff");
            this.put("aiff", "audio/x-aiff");
            this.put("aifc", "audio/x-aiff");
            this.put("m3u", "audio/x-mpegurl");
            this.put("ram", "audio/x-pn-realaudio");
            this.put("rm", "audio/x-pn-realaudio");
            this.put("rpm", "audio/x-pn-realaudio-plugin");
            this.put("ra", "audio/x-realaudio");
            this.put("wav", "audio/x-wav");
            this.put("pdb", "chemical/x-pdb");
            this.put("xyz", "chemical/x-xyz");
            this.put("bmp", "image/bmp");
            this.put("gif", "image/gif");
            this.put("ief", "image/ief");
            this.put("jpeg", "image/jpeg");
            this.put("jpg", "image/jpeg");
            this.put("jpe", "image/jpeg");
            this.put("png", "image/png");
            this.put("tiff", "image/tiff");
            this.put("tif", "image/tiff");
            this.put("djvu", "image/vnd.djvu");
            this.put("djv", "image/vnd.djvu");
            this.put("wbmp", "image/vnd.wap.wbmp");
            this.put("ras", "image/x-cmu-raster");
            this.put("pnm", "image/x-portable-anymap");
            this.put("pbm", "image/x-portable-bitmap");
            this.put("pgm", "image/x-portable-graymap");
            this.put("ppm", "image/x-portable-pixmap");
            this.put("rgb", "image/x-rgb");
            this.put("xbm", "image/x-xbitmap");
            this.put("xpm", "image/x-xpixmap");
            this.put("xwd", "image/x-xwindowdump");
            this.put("igs", "model/iges");
            this.put("iges", "model/iges");
            this.put("msh", "model/mesh");
            this.put("mesh", "model/mesh");
            this.put("silo", "model/mesh");
            this.put("wrl", "model/vrml");
            this.put("vrml", "model/vrml");
            this.put("css", "text/css");
            this.put("html", "text/html");
            this.put("htm", "text/html");
            this.put("asc", "text/plain");
            this.put("txt", "text/plain");
            this.put("rtx", "text/richtext");
            this.put("rtf", "text/rtf");
            this.put("sgml", "text/sgml");
            this.put("sgm", "text/sgml");
            this.put("tsv", "text/tab-separated-values");
            this.put("wml", "text/vnd.wap.wml");
            this.put("wmls", "text/vnd.wap.wmlscript");
            this.put("etx", "text/x-setext");
            this.put("xsl", "text/xml");
            this.put("xml", "text/xml");
            this.put("mpeg", "video/mpeg");
            this.put("mpg", "video/mpeg");
            this.put("mpe", "video/mpeg");
            this.put("qt", "video/quicktime");
            this.put("mov", "video/quicktime");
            this.put("mxu", "video/vnd.mpegurl");
            this.put("avi", "video/x-msvideo");
            this.put("movie", "video/x-sgi-movie");
            this.put("ice", "x-conference/x-cooltalk");
        }
    };

    @Autowired
    private StorageService storageService;
    @Autowired
    private MultipartResolver multipartResolver;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StorageRecordConvertor storageRecordConvertor;

    /**
     * 文件上传
     *
     * @param file
     * @return
     * @throws IOException
     */
    @Timed
    @RequestMapping(value = "/filestorage/upload", method = RequestMethod.POST)
    public ListResultDTO<StorageRecordDTO> upload(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        final List<StorageRecord> records = new ArrayList<>();
        if (this.multipartResolver.isMultipart(request)) {
            records.addAll(this.uploadFile(request));
        } else if (request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
            final InputStream in = request.getInputStream();
            final UploadDTO dto = this.objectMapper.readValue(in, UploadDTO.class);
            if ((dto.getBase64() != null) && (Boolean.TRUE == dto.getBase64())) {
                // base64格式的图片
                records.add(this.uploadBase64Image(dto));
            } else {
                records.add(this.uploadJson(dto));
            }
        } else {
            throw new UnsupportedOperationException();
        }
        records.forEach(record -> {
            this.storageService.normalize(record.getId(), true);
        });
        return this.storageRecordConvertor.toResultDTO(records);
    }

    /**
     * 文件上传
     *
     * @param request
     * @return
     * @throws IOException
     */
    private List<StorageRecord> uploadFile(final HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest multiReq = null;
        if (!(request instanceof MultipartHttpServletRequest)) {
            multiReq = this.multipartResolver.resolveMultipart(request);
        } else {
            multiReq = (MultipartHttpServletRequest) request;
        }
        final List<StorageRecord> records = new ArrayList<>();
        final Map<String, MultipartFile> files = multiReq.getFileMap();

        if (this.isChunkedUpload(multiReq)) {
            final StorageRecord record = this.handleChunkedUpload(multiReq);
            if (record != null) {
                records.add(record);
            }
        } else {
            files.values().forEach(file -> {
                records.add(this.storageService.save(file));
            });
        }
        return records;
    }

    private boolean isChunkedUpload(final MultipartHttpServletRequest multiReq) {
        return StringUtils.hasText(multiReq.getParameter("flowIdentifier"));
    }

    private StorageRecord handleChunkedUpload(final MultipartHttpServletRequest multiReq) throws IOException {
        final String flowIdentifier = multiReq.getParameter("flowIdentifier");
        final String flowFilename = multiReq.getParameter("flowFilename");
        final int flowTotalChunks = Integer.parseInt(multiReq.getParameter("flowTotalChunks"));
        final int flowChunkNumber = Integer.parseInt(multiReq.getParameter("flowChunkNumber"));
        final long flowTotalSize = Long.parseLong(multiReq.getParameter("flowTotalSize"));

        if (flowTotalChunks == 1) {
            final MultipartFile fileItem = multiReq.getFileMap().values().iterator().next();
            return this.storageService.save(fileItem); // FIXME
        }

        // 临时目录用来存放所有分片文件
        final String day = DateFormatUtils.format(new Date(), "yyyyMMdd");
        final String parentFileDir = day + File.separator + flowIdentifier;

        final MultipartFile tempFileItem = multiReq.getFileMap().values().iterator().next();
        // 分片处理时，前台会多次调用上传接口，每次都会上传文件的一部分到后台(默认每片为5M)
        final File tempPartFile =
                this.storageService.createLocalTempFile(parentFileDir, flowFilename + "_" + flowChunkNumber + ".part");
        tempFileItem.transferTo(tempPartFile);
        //FileUtils.copyInputStreamToFile(tempFileItem.getInputStream(), tempPartFile);
        // 是否全部上传完成,所有分片都存在才说明整个文件上传完成
        boolean uploadDone = true;

        final File[] files = new File[flowTotalChunks];

        for (int i = 1; i <= flowTotalChunks; i++) {
            final File partFile = this.storageService.getLocalTempFile(parentFileDir, flowFilename + "_" + i + ".part");
            if (!partFile.exists()) {
                uploadDone = false;
                break;
            }
            files[i - 1] = partFile;
        }
        // 将所有分片文件合并到一个文件中
        if (uploadDone) {
            return this.storageService.save(files, flowFilename, flowTotalSize);
        } else {
            return null;
        }
    }

    /**
     * 文件下载
     *
     * @param filePath 文件路径（必须）
     * @param extension 文件扩展名（非必须，需要与存储时的扩展名一致）
     * @param fileName 文件名（非必须，若非空，则为下载后文件名）
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Timed
    @RequestMapping(value = "/filestorage/download/{id}", method = RequestMethod.GET)
    public void download(
            @PathVariable final Long id,
            @RequestParam(required = false) final NormalizationType type,
            @RequestParam(required = false) final String fileName,
            @RequestParam(required = false) final String mediaType,
            final HttpServletRequest request,
            final HttpServletResponse response) throws FileNotFoundException, IOException {
        final StorageRecord record = this.storageService.getFileStorageRecord(id);
        if ((mediaType != null) && mediaType.equals("base64") && record.getFileName().endsWith(".json")) {
            this.downloadBase64Image(record, response);
            return;
        }
        if ((mediaType != null) && mediaType.equals("json")) {
            this.downloadJson(record, response);
            return;
        }
        String name = fileName;
        if ((name != null) && Base64.isBase64(name)) {
            name = new String(Base64Utils.decodeFromUrlSafeString(fileName), Charset.forName("UTF-8"));
        }
        if (name == null) {
            name = record.getFileName();
        }
        final File file = this.storageService.getFile(record, type == null ? NormalizationType.standard : type);
        if (name == null) {
            name = file.getName();
        }
        this.breakPointDownload(request, response, name, file);
    }

    private void breakPointDownload(final HttpServletRequest request, final HttpServletResponse response,
            String fileName, final File file) throws IOException, FileNotFoundException {
        final Long length = file.length();
        final Long lastModified = file.lastModified();
        final String ETag = file.getName();
        final String ifNoneMatch = request.getHeader("If-None-Match");

        {
            // Validate request headers for caching
            // ---------------------------------------------------
            // If-None-Match header should contain "*" or ETag. If so, then
            // return 304.
            if ((ifNoneMatch != null) && this.matches(ifNoneMatch, ETag)) {
                response.setHeader("ETag", ETag); // Required in 304.
                response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        }

        {
            // If-Modified-Since header should be greater than LastModified. If
            // so, then return 304.
            // This header is ignored if any If-None-Match header is specified.
            final long ifModifiedSince = request.getDateHeader("If-Modified-Since");
            if ((ifNoneMatch == null) && (ifModifiedSince != -1) && ((ifModifiedSince + 1000) > lastModified)) {
                response.setHeader("ETag", ETag); // Required in 304.
                response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        }

        {
            // Validate request headers for resume
            // ----------------------------------------------------
            // If-Match header should contain "*" or ETag. If not, then return
            // 412.
            final String ifMatch = request.getHeader("If-Match");
            if ((ifMatch != null) && !this.matches(ifMatch, ETag)) {
                response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
                return;
            }
        }

        {
            // If-Unmodified-Since header should be greater than LastModified.
            // If not, then return 412.
            final long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
            if ((ifUnmodifiedSince != -1) && ((ifUnmodifiedSince + 1000) <= lastModified)) {
                response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
                return;
            }
        }

        final Range full = new Range(0, length - 1, length);
        final List<Range> ranges = this.getRanges(ETag, request, response, length, full);
        if (ranges == null) {
            return;
        }

        if (!StringUtils.hasText(fileName)) {
            fileName = file.getName();
        }
        final String contentType = LocalStoreController.contentTypes.get(FilenameUtils.getExtension(fileName));
        {
            // Initialize response.
            fileName = LocalStoreController.encodeFilename(request, fileName);
            response.setBufferSize(LocalStoreController.DEFAULT_BUFFER_SIZE);

            response.setContentType(contentType);
            if ((contentType == null) || (!contentType.startsWith("video") && !contentType.startsWith("audio"))) {
                //response.setHeader("Content-Disposition", "attachment;" + fileName);
            }
            response.setHeader("Content-Disposition", "attachment;" + fileName);
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("ETag", ETag);
            response.setDateHeader("Last-Modified", lastModified);
            response.setDateHeader("Expires", System.currentTimeMillis() + LocalStoreController.DEFAULT_EXPIRE_TIME);
        }

        this.writeResponse(response, contentType, file, full, ranges);
    }

    /**
     * Base64格式图片上传
     *
     * @param dto
     * @return
     * @throws IOException
     */
    private StorageRecord uploadBase64Image(final UploadDTO dto) {
        if (!StringUtils.hasText(dto.getExtension())) {
            throw new CustomRuntimeException("400", "extension不能为空.");
        }
        final byte[] imageBytes = Base64Utils.decodeFromString(dto.getBody());
        final String originalName = this.getRandomFileName(dto.getExtension());
        final BytesMultipartFile file = new BytesMultipartFile(imageBytes, originalName);
        return this.storageService.save(file);
    }

    /**
     * Base64格式图片下载
     *
     * @param dto
     * @return
     * @throws JsonMappingException
     * @throws JsonGenerationException
     * @throws IOException
     */
    private void downloadBase64Image(final StorageRecord record, final HttpServletResponse response)
            throws IOException {
        final File image = this.storageService.getFile(record, NormalizationType.original);
        final String imageBase64 = Base64Utils.encodeToString(Files.readAllBytes(image.toPath()));
        final UploadDTO dto = new UploadDTO();
        dto.setBase64(true);
        dto.setBody(imageBase64);
        dto.setExtension(FilenameUtils.getExtension(image.getName()));
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        this.objectMapper.writeValue(response.getOutputStream(), dto);
    }

    /**
     * 文本上传
     *
     * @param dto
     * @return
     * @throws IOException
     */
    private StorageRecord uploadJson(@RequestBody final UploadDTO dto) throws IOException {
        final byte[] jsonBytes = this.objectMapper.writeValueAsBytes(dto);
        final String originalName = this.getRandomFileName("json");
        final BytesMultipartFile file = new BytesMultipartFile(jsonBytes, originalName);
        return this.storageService.save(file);
    }

    /**
     * JSON文本下载
     *
     * @param dto
     * @return
     * @throws IOException
     */
    private void downloadJson(final StorageRecord record, final HttpServletResponse response) throws IOException {
        final File jsonFile = this.storageService.getFile(record, NormalizationType.original);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.getOutputStream().write(Files.readAllBytes(jsonFile.toPath()));
    }

    private void writeResponse(final HttpServletResponse response, final String contentType, final File file,
            final Range full, final List<Range> ranges) throws FileNotFoundException, IOException {
        // Send requested file (part(s)) to client
        // ------------------------------------------------
        // Prepare streams.
        final Long length = file.length();
        InputStream input = null;
        try (OutputStream output = response.getOutputStream()) {
            input = new BufferedInputStream(new FileInputStream(file));
            if (ranges.isEmpty() || (ranges.get(0) == full)) {
                // Return full file.
                response.setHeader("Content-Range", "bytes " + full.start + "-" + full.end + "/" + full.total);
                response.setHeader("Content-Length", String.valueOf(full.length));
                Range.copy(input, output, length, full.start, full.length);
            } else if (ranges.size() == 1) {
                // Return single part of file.
                final Range r = ranges.get(0);
                response.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/" + r.total);
                response.setHeader("Content-Length", String.valueOf(r.length));
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.
                // Copy single part range.
                Range.copy(input, output, length, r.start, r.length);
            } else {
                // Return multiple parts of file.
                response.setContentType("multipart/byteranges; boundary=" + LocalStoreController.MULTIPART_BOUNDARY);
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.
                // Cast back to ServletOutputStream to get the easy println
                // methods.
                final ServletOutputStream sos = (ServletOutputStream) output;
                // Copy multi part range.
                for (final Range r : ranges) {
                    // Add multipart boundary and header fields for every range.
                    sos.println();
                    sos.println("--" + LocalStoreController.MULTIPART_BOUNDARY);
                    sos.println("Content-Type, " + contentType);
                    sos.println("Content-Range, bytes " + r.start + "-" + r.end + "/" + r.total);
                    // Copy single part range of multi part range.
                    Range.copy(input, output, length, r.start, r.length);
                }
                // End with multipart boundary.
                sos.println();
                sos.println("--" + LocalStoreController.MULTIPART_BOUNDARY + "--");
            }
        } catch (final Exception e) {
            // 可能连接被重置
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    private List<Range> getRanges(final String ETag, final HttpServletRequest request,
            final HttpServletResponse response, final Long length, final Range full) throws IOException {

        // Prepare some variables. The full Range represents the complete file.
        final List<Range> ranges = new ArrayList<>();

        // Validate and process Range and If-Range headers.
        final String range = request.getHeader("Range");
        if (range != null) {
            // Range header should match format "bytes=n-n,n-n,n-n...". If not,
            // then return 416.
            if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
                response.setHeader("Content-Range", "bytes */" + length); // Required in 416.
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return null;
            }

            final String ifRange = request.getHeader("If-Range");
            if ((ifRange != null) && !ifRange.equals(ETag)) {
                try {
                    final long ifRangeTime = request.getDateHeader("If-Range"); // Throws IAE if invalid.
                    if (ifRangeTime != -1) {
                        ranges.add(full);
                    }
                } catch (final IllegalArgumentException ignore) {
                    ranges.add(full);
                }
            }

            // If any valid If-Range header, then process each part of byte
            // range.
            if (ranges.isEmpty()) {
                for (final String part : range.substring(6).split(",")) {
                    // Assuming a file with length of 100, the following
                    // examples returns bytes at,
                    // 50-80 (50 to 80), 40- (40 to length=100), -20
                    // (length-20=80 to length=100).
                    long start = Range.sublong(part, 0, part.indexOf("-"));
                    long end = Range.sublong(part, part.indexOf("-") + 1, part.length());

                    if (start == -1) {
                        start = length - end;
                        end = length - 1;
                    } else if ((end == -1) || (end > (length - 1))) {
                        end = length - 1;
                    }

                    // Check if Range is syntactically valid. If not, then
                    // return 416.
                    if (start > end) {
                        response.setHeader("Content-Range", "bytes */" + length); // Required  in 416.
                        response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                        return null;
                    }

                    // Add range.
                    ranges.add(new Range(start, end, length));
                }
            }
        }
        return ranges;
    }

    public static String encodeFilename(final HttpServletRequest request, final String filename) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            userAgent = "";
        }
        try {
            userAgent = userAgent.toLowerCase();
            if ((userAgent.indexOf("msie") != -1)) {//|| (userAgent.indexOf("like gecko") != -1)) {
                // IE浏览器，只能采用URLEncoder编码
                String name = java.net.URLEncoder.encode(filename, LocalStoreController.DEFAULT_CHARSET);
                name = StringUtils.replace(filename, "+", "%20");//替换空格
                return "filename=\"" + name + "\"";
            } else if (userAgent.indexOf("opera") != -1) {
                // Opera浏览器只能采用filename*
                return "filename*=UTF-8''" + filename;
            } else if (userAgent.indexOf("safari") != -1) {
                // Safari浏览器，只能采用ISO编码的中文输出
                return "filename=\"" + new String(filename.getBytes(LocalStoreController.DEFAULT_CHARSET), "ISO8859-1")
                        + "\"";
            } else if (userAgent.indexOf("applewebkit") != -1) {
                // Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
                final String newFilename = MimeUtility.encodeText(filename, LocalStoreController.DEFAULT_CHARSET, "B");
                return "filename=\"" + newFilename + "\"";
            } else if (userAgent.indexOf("mozilla") != -1) {
                // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
                return "filename*=UTF-8''" + filename;
            } else {
                String name = java.net.URLEncoder.encode(filename, LocalStoreController.DEFAULT_CHARSET);
                name = StringUtils.replace(filename, "+", "%20");//替换空格
                return "filename=\"" + name + "\"";
            }
        } catch (final UnsupportedEncodingException e) {
            return "filename=\"" + filename + "\"";
        }
    }

    private boolean matches(final String matchHeader, final String toMatch) {
        final String[] matchValues = matchHeader.split("\\s*,\\s*");
        Arrays.sort(matchValues);
        return (Arrays.binarySearch(matchValues, toMatch) > -1) || (Arrays.binarySearch(matchValues, "*") > -1);
    }

    private String getRandomFileName(final String extension) {
        final int random = (int) Math.random() * 100000;
        return String.valueOf(random) + "." + extension;
    }

    public static class UploadDTO {
        @NotNull
        private String body;
        private String extension;
        private Boolean base64;
        private Long id;

        public String getBody() {
            return this.body;
        }

        public void setBody(final String body) {
            this.body = body;
        }

        public String getExtension() {
            return this.extension;
        }

        public void setExtension(final String extension) {
            this.extension = extension;
        }

        public Boolean getBase64() {
            return this.base64;
        }

        public void setBase64(final Boolean base64) {
            this.base64 = base64;
        }

        public Long getId() {
            return this.id;
        }

        public void setId(final Long id) {
            this.id = id;
        }
    }

    public static class BytesMultipartFile implements MultipartFile {

        private final ByteArrayInputStream inputStream;

        private final String DEFAULT_NAME = "base64.jpg";

        private String originalFilename;

        private String name;

        private long size;

        public BytesMultipartFile(final byte[] bytes, final String originalName) {
            if ((bytes == null) || (bytes.length == 0)) {
                this.size = 0;
                this.inputStream = new ByteArrayInputStream(new byte[0]);
            } else {
                this.size = bytes.length;
                this.inputStream = new ByteArrayInputStream(bytes);
            }
            if (StringUtils.hasText(originalName)) {
                this.originalFilename = originalName;
                this.name = FilenameUtils.getName(this.originalFilename);
            } else {
                this.originalFilename = this.name = this.DEFAULT_NAME;
            }
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getOriginalFilename() {
            return this.originalFilename;
        }

        @Override
        public String getContentType() {
            return "base64";
        }

        @Override
        public boolean isEmpty() {
            return this.size == 0;
        }

        @Override
        public long getSize() {
            return this.size;
        }

        @Override
        public byte[] getBytes() throws IOException {
            final byte[] bytes = new byte[(int) this.size];
            this.inputStream.read(bytes);
            return bytes;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return this.inputStream;
        }

        @Override
        public void transferTo(final File dest) throws IOException, IllegalStateException {
            final FileOutputStream out = new FileOutputStream(dest);
            IOUtils.copy(this.inputStream, out);
            IOUtils.closeQuietly(out);
        }

    }

    private static class Range {
        long start;
        long end;
        long length;
        long total;

        /**
         * Construct a byte range.
         *
         * @param start Start of the byte range.
         * @param end End of the byte range.
         * @param total Total length of the byte source.
         */
        public Range(final long start, final long end, final long total) {
            this.start = start;
            this.end = end;
            this.length = (end - start) + 1;
            this.total = total;
        }

        public static long sublong(final String value, final int beginIndex, final int endIndex) {
            final String substring = value.substring(beginIndex, endIndex);
            return (substring.length() > 0) ? Long.parseLong(substring) : -1;
        }

        private static void copy(final InputStream input, final OutputStream output, final long inputSize,
                final long start, final long length) throws IOException {
            final byte[] buffer = new byte[LocalStoreController.DEFAULT_BUFFER_SIZE];
            int read;

            if (inputSize == length) {
                // Write full range.
                while ((read = input.read(buffer)) > 0) {
                    output.write(buffer, 0, read);
                    output.flush();
                }
            } else {
                input.skip(start);
                long toRead = length;

                while ((read = input.read(buffer)) > 0) {
                    if ((toRead -= read) > 0) {
                        output.write(buffer, 0, read);
                        output.flush();
                    } else {
                        output.write(buffer, 0, (int) toRead + read);
                        output.flush();
                        break;
                    }
                }
            }
        }
    }
}
