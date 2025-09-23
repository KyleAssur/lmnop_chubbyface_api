package za.ac.cput.service;

import za.ac.cput.domain.Admin;

import java.util.List;

public interface IAdminService extends IService<Admin, Long>{
    boolean delete(long messageId);
    List<Admin> getAll();
}
