package com.finflux.pdcm.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.pdcm.exception.PostDatedChequeDetailNotFoundException;

@Service
public class PostDatedChequeDetailsRepositoryWrapper {

    private final PostDatedChequeDetailsRepository repository;

    @Autowired
    public PostDatedChequeDetailsRepositoryWrapper(final PostDatedChequeDetailsRepository repository) {
        this.repository = repository;
    }

    public PostDatedChequeDetail findOneWithNotFoundDetection(final Long pdcId) {
        final PostDatedChequeDetail postDatedChequeDetail = this.repository.findOne(pdcId);
        if (postDatedChequeDetail == null) { throw new PostDatedChequeDetailNotFoundException(pdcId); }
        return postDatedChequeDetail;
    }

    public void save(final PostDatedChequeDetail postDatedChequeDetail) {
        this.repository.save(postDatedChequeDetail);
    }

    public void save(final List<PostDatedChequeDetail> postDatedChequeDetails) {
        this.repository.save(postDatedChequeDetails);
    }

    public void saveAndFlush(final PostDatedChequeDetail postDatedChequeDetail) {
        this.repository.saveAndFlush(postDatedChequeDetail);
    }

    public void delete(final PostDatedChequeDetail postDatedChequeDetail) {
        this.repository.delete(postDatedChequeDetail);
    }
}
