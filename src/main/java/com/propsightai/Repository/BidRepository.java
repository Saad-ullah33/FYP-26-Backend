package com.propsightai.Repository;

import com.propsightai.Model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid,Integer> {
}
