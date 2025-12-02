package com.nexsol.tpa.core.domain;

import java.util.Optional;

public interface UserRepository {

	Optional<User> findByCompanyCodeAndEmail(String companyCode, String email);

	boolean existsCompanyCodeAndEmail(String companyCode, String email);

	User save(User user);

}
