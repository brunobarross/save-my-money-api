package com.altamirobruno.save_my_money.dto.mappers;

import com.altamirobruno.save_my_money.dto.UserDTO;
import com.altamirobruno.save_my_money.dto.WalletDTO;
import com.altamirobruno.save_my_money.exceptions.ItemNotFoundException;
import com.altamirobruno.save_my_money.model.User;
import com.altamirobruno.save_my_money.model.Wallet;
import com.altamirobruno.save_my_money.repository.UserRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
public class WalletMapper {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public WalletDTO toDTO(Wallet wallet, BigDecimal amount) {
        UserDTO userDTO = wallet.getUser() == null ? null : userMapper.toDTO(wallet.getUser());
        if (userDTO == null) throw new AssertionError();
        return new WalletDTO(wallet.getId(), wallet.getName(), wallet.getColor(), userDTO.id(), wallet.getIcon(), amount);

    }

    public Wallet toEntity(WalletDTO walletDTO) {
        if (walletDTO == null) {
            return null;
        }

        Wallet wallet = new Wallet();
        if (walletDTO.id() != null) {
            wallet.setId(walletDTO.id());
        }

        wallet.setName(walletDTO.name());
        wallet.setColor(walletDTO.color());
        wallet.setIcon(walletDTO.icon());
        wallet.setAmount(walletDTO.amount() == null ? BigDecimal.ZERO : walletDTO.amount());
        if (walletDTO.userId() != null) {
            User user = userRepository.findById(walletDTO.userId())
                    .orElseThrow(() -> new ItemNotFoundException(walletDTO.userId()));
            wallet.setUser(user);
        }

        return wallet;

    }
}
