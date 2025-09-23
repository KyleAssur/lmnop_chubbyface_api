package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Support;
import za.ac.cput.repository.SupportRepository;

import java.util.List;

@Service
public class SupportService implements ISupportService {

    private final SupportRepository contactSupportRepository;

    @Autowired
    public SupportService(SupportRepository contactSupportRepository) {
        this.contactSupportRepository = contactSupportRepository;
    }

    public Support create(Support contactSupport) {
        return null;
    }

    public Support read(Long messageId) {
        return null;
    }

    public Support update(Support contactSupport) {
        return null;
    }

    public void delete(long messageId) {
        //TODO
    }


    public List<Support> getAll() {
        return null;
    }
}
