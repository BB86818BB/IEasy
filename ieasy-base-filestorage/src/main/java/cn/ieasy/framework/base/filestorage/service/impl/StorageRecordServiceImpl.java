package cn.ieasy.framework.base.filestorage.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.ieasy.framework.base.filestorage.model.StorageRecord;
import cn.ieasy.framework.base.filestorage.repository.StorageRecordRepository;
import cn.ieasy.framework.base.filestorage.service.StorageRecordService;
import cn.ieasy.framework.common.web.exception.CustomRuntimeException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * StorageRecordService 实现类
 */
@Service
@Transactional
@Slf4j
public class StorageRecordServiceImpl implements StorageRecordService {

    private final Pattern numberPattern = Pattern.compile("\\d+");

    private String serverId;
    @Autowired
    private StorageRecordRepository storageRecordRepository;

    //    @Value("${local.storage.publicIP}")
    //    String publicIP;

    //    @Value("${server.port}")
    //    String port;

    @Override
    public StorageRecord getStorageRecord(@NonNull final Long id) {
        final StorageRecord model = this.storageRecordRepository.findOne(id);
        if (model == null) {
            throw new CustomRuntimeException("404", String.format("查找的资源[%s]不存在.", id));
        }
        return model;
    }

    @Override
    public StorageRecord createStorageRecord(final StorageRecord model) {
        return this.storageRecordRepository.save(model);
    }

    @Override
    public StorageRecord updateStorageRecord(final StorageRecord model) {
        return this.storageRecordRepository.save(model);
    }

    @Override
    public void deleteStorageRecord(@NonNull final Long id) {
        this.storageRecordRepository.delete(id);
    }

    @Override
    public List<StorageRecord> getStorageRecords(final List<Long> storageIds) {
        if (storageIds.isEmpty()) {
            return Collections.emptyList();
        }
        return this.storageRecordRepository.findAllByIdIn(storageIds);
    }

    //
    //    @Override
    //    public String getPublicIP(){
    //        if(!this.publicIP.contains("*"))
    //            return this.publicIP;
    //        Enumeration<NetworkInterface> interfaces;
    //        try {
    //            interfaces = NetworkInterface.getNetworkInterfaces();
    //        } catch (final SocketException e) {
    //            log.error("获取网卡信息失败！", e);
    //            return null;
    //        }
    //        while (interfaces.hasMoreElements()) {
    //            final Enumeration<InetAddress> inetAddresses = interfaces.nextElement().getInetAddresses();
    //            while (inetAddresses.hasMoreElements()) {
    //                final InetAddress ip = inetAddresses.nextElement();
    //                final String ipStr = ip.getHostAddress();
    //                if (this.matches(ipStr, this.publicIP)) {
    //                    return ipStr;
    //                }
    //            }
    //        }
    //        return null;
    //    }

    //    private boolean matches(final String ipAddress, final String network) {
    //        final String[] ipElements = ipAddress.split("\\.");
    //        final String[] networkElements = network.split("\\.");
    //        if (ipElements.length != networkElements.length) {
    //            return false;
    //        }
    //        for (int i = 0; i < ipElements.length; i++) {
    //            if (!ipElements[i].equals(networkElements[i]) && !networkElements[i].equals("*")) {
    //                return false;
    //            }
    //        }
    //        return true;
    //    }

    //    @Override
    //    public String getPort(){
    //        return this.port;
    //    }
}
